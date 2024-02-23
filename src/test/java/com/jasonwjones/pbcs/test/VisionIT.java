package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.client.*;
import com.jasonwjones.pbcs.client.exceptions.PbcsInvalidDimensionException;
import com.jasonwjones.pbcs.client.exceptions.PbcsJobLaunchException;
import com.jasonwjones.pbcs.client.impl.PlanTypeConfigurationImpl;
import com.jasonwjones.pbcs.utils.PbcsClientUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThrows;

public class VisionIT {

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
    public void whenInvalidBusinessRuleRequestedThenThrowException() {
        final String invalidRule = "SomeInvalidRule";
        PbcsJobLaunchException exception = assertThrows(PbcsJobLaunchException.class, () -> app.launchBusinessRule(invalidRule));
        assertThat(exception.getJobName(), is(invalidRule));
    }

    @Test
    public void whenLaunchValidRuleThenReturnsInProgress() {
        PbcsJobLaunchResult result = app.launchBusinessRule("calcall");
        assertThat(result.getJobStatusType(), is(PbcsJobStatusCode.IN_PROGRESS));
    }

    @Test
    public void whenGetRules() {
        List<PbcsJobDefinition> rules = app.getJobDefinitions(PbcsJobType.RULES);
        List<String> jobNames = rules.stream()
                .map(r -> r.getJobName())
                .collect(Collectors.toList());
        assertThat(jobNames, hasItem("calcall"));
    }

}