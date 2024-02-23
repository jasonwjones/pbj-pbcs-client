package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.client.*;
import com.jasonwjones.pbcs.client.exceptions.PbcsInvalidDimensionException;
import com.jasonwjones.pbcs.client.exceptions.PbcsJobLaunchException;
import com.jasonwjones.pbcs.client.exceptions.PbcsNoSuchObjectException;
import com.jasonwjones.pbcs.client.impl.PlanTypeConfigurationImpl;
import com.jasonwjones.pbcs.utils.PbcsClientUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class VisionIT {

    private static final Logger logger = LoggerFactory.getLogger(VisionIT.class);

    @SuppressWarnings("SpellCheckingInspection")
    private static final String CALC_ALL = "calcall";

    public static final List<String> DIMENSIONS = Arrays.asList("Account", "Currency", "Entity", "Period", "Product", "Scenario", "Version", "Year");

    public static final List<String> INVALID_DIMENSIONS = Arrays.asList("Invalid1", "Invalid2");

    protected PbcsApplication app;

    @Before
    public void setUp() {
        app = PbcsClientUtils.vision();
    }

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
                .map(PbcsJobDefinition::getJobName)
                .collect(Collectors.toList());
        assertThat(jobNames, hasItem(CALC_ALL));
    }

    @Test
    public void whenRefreshCube() throws InterruptedException {
        PbcsJobStatus job = app.refreshCube().waitUntilFinished();
        assertTrue(job.isSuccessful());
    }

    @Test
    public void whenGetValidMember() {
        PbcsMemberProperties member = app.getMember("Account", "Cash from Current Operations");
        assertThat(member.getDimensionName(), is("Account"));
        printMember(member, 0);
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
        PbcsNoSuchObjectException exception = assertThrows(PbcsNoSuchObjectException.class, () -> app.getClient().getApplication(invalidApplicationName));
        assertThat(exception.getObjectName(), is(invalidApplicationName));
        assertThat(exception.getObjectType(), is(PbcsObjectType.APPLICATION));
    }

    private static void printMember(PbcsMemberProperties member, int level) {
        for (int i = 0; i < level; i++) System.out.print("    ");
        System.out.printf("%s (%s) %n", member.getName(), member.getDataStorage());

        for (PbcsMemberProperties child : member.getChildren()) {
            printMember(child, level + 1);
        }
    }

}