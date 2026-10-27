package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.api.v3.SubstitutionVariable;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsApplicationSubstitutionVariable;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.client.PbcsPlanTypeSubstitutionVariable;
import com.jasonwjones.pbcs.client.PbcsPlanningClient;
import com.jasonwjones.pbcs.client.PbcsSubstitutionVariable;
import com.jasonwjones.pbcs.client.PbcsSubstitutionVariableScope;
import com.jasonwjones.pbcs.util.ConnectionUtils;
import com.jasonwjones.pbcs.testing.DestructiveIntegrationTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Category(DestructiveIntegrationTest.class)
public class PbcsPlanTypeSubVarIT extends AbstractVisionIT {

    private static final String TEST_NAME = "_Test";

    private static final String TEST_VALUE = "test_value";

    @BeforeClass
    public static void beforeClass() {
        PbcsPlanningClient client = new PbcsClientFactory().createClient(ConnectionUtils.defaultConnection());
        PbcsApplication app = client.getApplication("Vision");
        app.updateVariable(TEST_NAME, TEST_VALUE);
    }

    @Test
    public void whenGetVariables() {
        List<PbcsSubstitutionVariable> variables = app.getVariables();
        variables.forEach(System.out::println);
        assertThat(variables, hasItem(allOf(
                hasProperty("name", is(TEST_NAME)),
                hasProperty("scope", is(PbcsSubstitutionVariableScope.APPLICATION)))));
        assertThat(variables, hasItem(allOf(
                hasProperty("name", is("Plan1Yr")),
                hasProperty("scope", is(PbcsSubstitutionVariableScope.PLAN_TYPE)),
                hasProperty("planTypeName", is("Plan1")))));
    }

    @Test
    public void whenUpdateVariable() {
        app.updateVariable(TEST_NAME, TEST_VALUE);
        PbcsApplicationSubstitutionVariable variable = app.getVariable(TEST_NAME);
        assertThat(variable.getValue(), is(TEST_VALUE));
        assertThat(variable.getScope(), is(PbcsSubstitutionVariableScope.APPLICATION));
    }

    @Test
    public void whenGetPlanTypeVariables() {
        PbcsPlanType plan = app.getPlanType("Plan1");
        List<PbcsPlanTypeSubstitutionVariable> variables = plan.getVariables();
        variables.forEach(System.out::println);
        assertThat(variables, hasItem(hasProperty("name", is("Plan1Yr"))));
        assertThat(variables, everyItem(hasProperty("planTypeName", is("Plan1"))));
    }

    @Test
    public void whenUpdatePlanTypeVariableToItsOwnValue() {
        PbcsPlanType plan = app.getPlanType("Plan1");
        PbcsPlanTypeSubstitutionVariable plan1Yr = plan.getVariable("Plan1Yr");

        plan.updateVariable(plan1Yr.getName(), plan1Yr.getValue());
        app.updateVariables(Collections.singletonList(plan1Yr.withValue(plan1Yr.getValue())));

        assertThat(plan.getVariable("Plan1Yr").getValue(), is(plan1Yr.getValue()));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void whenGetDeprecated() {
        Set<SubstitutionVariable> subVars = app.getSubstitutionVariables();
        subVars.forEach(System.out::println);
        assertThat(subVars, hasItem(hasProperty("name", is(TEST_NAME))));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void whenUpdateVariableDeprecated() {
        app.updateSubstitutionVariable(TEST_NAME, TEST_VALUE);
        SubstitutionVariable subVar = app.getSubstitutionVariable(TEST_NAME);
        assertThat(subVar.getValue(), is(TEST_VALUE));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void whenGetPlanVariablesDeprecated() {
        PbcsPlanType plan = app.getPlanType("Plan1");
        Set<SubstitutionVariable> subVars = plan.getSubstitutionVariables();
        assertThat(subVars, hasItem(hasProperty("name", is("Plan1Yr"))));
        subVars.forEach(System.out::println);
    }

}
