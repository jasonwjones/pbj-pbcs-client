package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.api.v3.UserPreferences;
import com.jasonwjones.pbcs.client.*;
import com.jasonwjones.pbcs.client.exceptions.PbcsInvalidDimensionException;
import com.jasonwjones.pbcs.client.exceptions.PbcsInvalidMemberException;
import com.jasonwjones.pbcs.client.exceptions.PbcsJobLaunchException;
import com.jasonwjones.pbcs.client.exceptions.PbcsNoSuchObjectException;
import org.hamcrest.CoreMatchers;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class PbcsApplicationImplIT extends AbstractVisionIT {

    private static final Logger logger = LoggerFactory.getLogger(PbcsApplicationImplIT.class);

    @SuppressWarnings("SpellCheckingInspection")
    private static final String CALC_ALL = "calcall";

    public static final List<String> INVALID_DIMENSIONS = Arrays.asList("Invalid1", "Invalid2");

    @Test
    public void whenInvalidDimensionsRequestedThenThrowException() {
        List<String> dimensions = new ArrayList<>(DIMENSIONS);
        dimensions.addAll(INVALID_DIMENSIONS);

        PlanTypeConfigurationImpl configuration = new PlanTypeConfigurationImpl();
        configuration.setName("Plan1");
        configuration.setSkipCheck(true);
        configuration.setValidateDimensions(true);
        configuration.setExplicitDimensions(dimensions);

        PbcsInvalidDimensionException exception = assertThrows(PbcsInvalidDimensionException.class, () -> app.getPlanType(configuration));
        assertThat(exception.getObjectName(), is(INVALID_DIMENSIONS.get(0)));
    }

    @Test
    public void whenListJobs() {
        List<PbcsJobDefinition> jobs = app.getJobDefinitions();
        assertThat(jobs, is(not(empty())));
        for (PbcsJobDefinition job : jobs) {
            logger.info("Job: {}", job);
        }
    }

    @Test
    public void whenInvalidBusinessRuleRequestedThenThrowException() {
        final String invalidRule = "SomeInvalidRule";
        PbcsJobLaunchException exception = assertThrows(PbcsJobLaunchException.class, () -> app.launchBusinessRule(invalidRule));
        assertThat(exception.getJobName(), is(invalidRule));
    }

    @Test
    public void whenLaunchValidRuleThenReturnsInProgress() {
        PbcsJobStatus result = app.launchBusinessRule(CALC_ALL);
        assertThat(result.getJobStatusType(), is(PbcsJobStatusCode.IN_PROGRESS));
    }

    @Test
    public void whenGetRulesThenHasSpecificRule() {
        List<PbcsJobDefinition> rules = app.getJobDefinitions(PbcsJobType.RULES);
        List<String> jobNames = rules.stream()
                .map(PbcsJobDefinition::getName)
                .toList();
        assertThat(jobNames, hasItem(CALC_ALL));
    }

    @Test
    public void whenRefreshCube() throws InterruptedException {
        PbcsJobStatus job = app.refreshCube().waitUntilFinished();
        assertTrue(job.isSuccessful());
    }

    @Test
    public void whenGetValidMember() {
        PbcsMember member = app.getMember("Account", "Cash from Current Operations");
        assertThat(member.getDimensionName(), is("Account"));
        logger.info("Qualified name: {}", member.getQualifiedName());
        printMember(member, 0);
    }

    @Test
    public void whenGetInvalidMember() {
        final String invalidMember = "__bad_member_4110X";
        PbcsInvalidMemberException exception = assertThrows(PbcsInvalidMemberException.class, () -> app.getMember("Account", invalidMember));
        assertThat(exception.getObjectName(), is(invalidMember));
    }

    @Test
    public void whenGetBaseOfDimension() {
        PbcsMember member = app.getMember("Version", "Version");
        assertThat(member.getParentName(), is(CoreMatchers.nullValue()));
    }

    @Test
    public void whenGetSharedMember() {
        PbcsMember member = app.getMember("Entity", "Sales Director 1");
        assertThat(member.getType(), is(PbcsMemberType.ENTITY));
        // has a single child, 240, that is shared
        assertThat(member.getChildren().get(0).getType(), is(PbcsMemberType.SHARED));
    }

    @Test
    public void testAppType() {
        assertThat(app.getAppType(), is(PbcsAppType.PLANNING));
    }

    @Test
    public void whenNoSuchPlanThenThrowException() {
        final String invalidPlanName = "InvalidPlan";
        PbcsNoSuchObjectException exception = assertThrows(PbcsNoSuchObjectException.class, () -> app.getPlanType(invalidPlanName));
        assertThat(exception.getObjectName(), is(invalidPlanName));
        assertThat(exception.getObjectType(), is(PbcsObjectType.PLAN));
    }

    @Test
    public void whenNoSuchApplicationThenThrowException() {
        final String invalidApplicationName = "InvalidApp";
        PbcsPlanningClient client = app.getClient();
        PbcsNoSuchObjectException exception = assertThrows(PbcsNoSuchObjectException.class, () -> client.getApplication(invalidApplicationName));
        assertThat(exception.getObjectName(), is(invalidApplicationName));
        assertThat(exception.getObjectType(), is(PbcsObjectType.APPLICATION));
    }

    @Test
    public void whenLaunchBusinessRule() throws InterruptedException {
        Map<String, String> params = new HashMap<>();
        params.put("RTP_Entity", "420");
        params.put("RTP_Product", "P_160");
        PbcsJobStatus status = app.launchBusinessRule("Calc_Payroll_Tax", params);
        PbcsJobStatus finalStatus = status.waitUntilFinished();
        assertThat(finalStatus.getJobStatusType(), is(PbcsJobStatusCode.SUCCESS));
    }

    @Test
    public void whenLaunchBusinessRuleMissingRuntimePrompt() {
        Map<String, String> params = new HashMap<>();
        params.put("RTP_Entity", "420");
        // we're missing a value for RTP_Product
        PbcsJobLaunchException exception = assertThrows(PbcsJobLaunchException.class, () -> app.launchBusinessRule("Calc_Payroll_Tax", params));
        assertThat(exception.getMessage(), is("Exception running job Calc_Payroll_Tax: Value is missing for the runtime prompt: RTP_Product."));
    }

    // Note: PBCS doesn't seem to care if you provide additional parameters that are unneeded. E.g., if you supply an
    // RTP value of "RTP_DoesntExist", it's just an extra parameter it doesn't care about
    @Test
    public void whenLaunchBusinessRuleWithInvalidPromptValue() {
        final String invalidMember = "420XX";
        Map<String, String> params = new HashMap<>();
        params.put("RTP_Entity", invalidMember);
        params.put("RTP_Product", "P_160");
        PbcsJobLaunchException exception = assertThrows(PbcsJobLaunchException.class, () -> app.launchBusinessRule("Calc_Payroll_Tax", params));
        assertThat(exception.getMessage(), is("Exception running job Calc_Payroll_Tax: The member " + invalidMember + " does not exist for the specified cube or you do not have access to it."));
    }

    @Test
    public void getUserPreferences() {
        UserPreferences prefs = (((PbcsApplicationImpl) app).getUserPreferences());
        System.out.println(prefs);
    }

    @Ignore
    @Test
    public void whenAddMember() {
        PbcsMember member = app.addMember("Entity", "North America", "Enterprise Global");
        assertThat(member.getName(), is("North America"));
        printMember(member, 0);
    }

    private static void printMember(PbcsMember member, int level) {
        for (int i = 0; i < level; i++) System.out.print("    ");
        System.out.printf("%s (%s) %n", member.getName(), member.getDataStorage());

        for (PbcsMember child : member.getChildren()) {
            printMember(child, level + 1);
        }
    }

}