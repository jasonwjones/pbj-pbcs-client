package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.api.v3.SubstitutionVariable;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.client.PbcsPlanningClient;
import com.jasonwjones.pbcs.util.ConnectionUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class PbcsPlanTypeSubVarIT extends AbstractVisionIT {

    private static final String TEST_NAME = "_Test";

    private static final String TEST_VALUE = "test_value";

    @BeforeClass
    public static void beforeClass() {
        PbcsPlanningClient client = new PbcsClientFactory().createClient(ConnectionUtils.defaultConnection());
        PbcsApplication app = client.getApplication("Vision");
        app.updateSubstitutionVariable(TEST_NAME, TEST_VALUE);
    }

    @Test
    public void whenGet() {
        Set<SubstitutionVariable> subVars = app.getSubstitutionVariables();
        subVars.forEach(System.out::println);
        assertThat(subVars, hasItem(hasProperty("name", is(TEST_NAME))));
    }

    @Test
    public void whenUpdateVariable() {
        app.updateSubstitutionVariable(TEST_NAME, TEST_VALUE);
        SubstitutionVariable subVar = app.getSubstitutionVariable(TEST_NAME);
        assertThat(subVar.getValue(), is(TEST_VALUE));
    }

    @Test
    public void whenGetPlanVariables() {
        PbcsPlanType plan = app.getPlanType("Plan1");
        Set<SubstitutionVariable> subVars = plan.getSubstitutionVariables();
        assertThat(subVars, hasItem(hasProperty("name", is("Plan1Yr"))));
        subVars.forEach(System.out::println);
    }

}