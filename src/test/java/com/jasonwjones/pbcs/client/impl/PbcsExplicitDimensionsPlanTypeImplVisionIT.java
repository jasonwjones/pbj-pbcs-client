package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.client.*;
import com.jasonwjones.pbcs.client.exceptions.PbcsInvalidDimensionException;
import com.jasonwjones.pbcs.testing.ReadOnlyIntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;

@Category(ReadOnlyIntegrationTest.class)
public class PbcsExplicitDimensionsPlanTypeImplVisionIT extends AbstractVisionIT {

    public static final String ATTRIBUTE_DIM_EXAMPLE = "Market Size";

    @Test
    public void whenInvalidDimension() {
        PbcsApplication.PlanTypeConfiguration configuration = new PlanTypeConfigurationImpl.Builder(PLAN)
                .build();
        PbcsPlanType planType = app.getPlanType(configuration);
        assertThat(planType.isExplicitDimensions(), is(false));
    }

    @Test
    public void whenCorrectExplicitDimensions() {
        PbcsApplication.PlanTypeConfiguration configuration = new PlanTypeConfigurationImpl.Builder(PLAN)
                .dimensions(DIMENSIONS)
                .build();
        PbcsPlanType planType = app.getPlanType(configuration);
        assertThat(planType.isExplicitDimensions(), is(true));
    }

    @Test
    public void whenGetPlanWithValidDimension() {
        PbcsApplication.PlanTypeConfiguration configuration = new PlanTypeConfigurationImpl.Builder("Plan1")
                .skipCheck()
                .dimensions(DIMENSIONS)
                .build();

        PbcsPlanType cube = app.getPlanType(configuration);
        assertThat(cube.getDimensions(), hasSize(DIMENSIONS.size()));
    }

    @Test
    public void whenGetInvalidDimensionFromPlan() {
        PbcsPlanType cube = app.getPlanType(planTypeConfiguration);
        final String badDimension = "BadDimension";
        PbcsInvalidDimensionException exception = assertThrows(PbcsInvalidDimensionException.class, () -> cube.getDimension(badDimension));
        assertThat(exception.getObjectName(), CoreMatchers.is(badDimension));
    }

    @Test
    public void whenGetPlanWithInvalidDimension() {
        final String badDimension = "BadDimension";
        PbcsApplication.PlanTypeConfiguration configuration = new PlanTypeConfigurationImpl.Builder("Plan1")
                .skipCheck()
                .dimensions(DIMENSIONS)
                .dimension(badDimension)
                .validateDimensions()
                .build();

        PbcsInvalidDimensionException exception = assertThrows(PbcsInvalidDimensionException.class, () -> app.getPlanType(configuration));
        assertThat(exception.getObjectName(), CoreMatchers.is(badDimension));
    }

    @Test
    @Ignore // attribute dimensions are ephemeral, for the moment
    public void whenGetPlanWithAttributeDimension() {
        PbcsApplication.PlanTypeConfiguration configuration = new PlanTypeConfigurationImpl.Builder("Plan1")
                .skipCheck()
                .dimensions(DIMENSIONS)
                .dimension(ATTRIBUTE_DIM_EXAMPLE)
                .validateDimensions()
                .build();

        PbcsPlanType plan = app.getPlanType(configuration);
        PbcsDimension dimension = plan.getDimension(ATTRIBUTE_DIM_EXAMPLE);
        assertThat(dimension.getDimensionType(), CoreMatchers.is(PbcsMemberType.ATTRIBUTE));
    }

    @Test
    public void whenQueryDimensionsThenValidCubeCreated() {
        PbcsApplication.PlanTypeConfiguration configuration = new PlanTypeConfigurationImpl.Builder("Plan1")
                .skipCheck()
                .queryDimensions()
                .validateDimensions()
                .build();

        PbcsPlanType plan = app.getPlanType(configuration);
        assertThat(plan.isExplicitDimensions(), CoreMatchers.is(true));
        assertThat(plan.getDimension("Scenario").getDimensionType(), CoreMatchers.is(PbcsMemberType.SCENARIO));
    }

    @Test
    public void whenDiscoverDimensionsThenValidCubeCreatedWithCorrectTypes() {
        PbcsApplication.PlanTypeConfiguration configuration = new PlanTypeConfigurationImpl.Builder("Plan1")
                .skipCheck()
                .discoverDimensions()
                .validateDimensions()
                .build();

        PbcsPlanType plan = app.getPlanType(configuration);
        assertThat(plan.isExplicitDimensions(), CoreMatchers.is(true));
        assertThat(plan.getDimension("Scenario").getDimensionType(), CoreMatchers.is(PbcsMemberType.SCENARIO));
    }

    @Test
    public void whenExplicitAttributeDimensionAlreadyDiscoveredThenNotDuplicated() {
        PlanTypeConfigurationImpl configuration = new PlanTypeConfigurationImpl();
        configuration.setName("Plan1");
        configuration.setSkipCheck(true);
        configuration.setDiscoverDimensions(true);
        // "Scenario" will already be discovered; redundantly declaring it as an explicit attribute
        // dimension should not add a second entry.
        configuration.setExplicitAttributeDimensions(List.of("Scenario"));

        PbcsPlanType plan = app.getPlanType(configuration);
        long scenarioCount = plan.getDimensions().stream().filter(d -> d.getName().equals("Scenario")).count();
        assertThat(scenarioCount, CoreMatchers.is(1L));
    }

}
