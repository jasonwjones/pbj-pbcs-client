package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.client.*;
import com.jasonwjones.pbcs.client.exceptions.PbcsInvalidDimensionException;
import com.jasonwjones.pbcs.testing.ReadOnlyIntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

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

}
