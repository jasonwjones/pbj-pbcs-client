package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsDimension;
import com.jasonwjones.pbcs.client.PbcsMemberType;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.client.exceptions.PbcsInvalidDimensionException;
import com.jasonwjones.pbcs.client.impl.PlanTypeConfigurationImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThrows;

public class VisionCubeInstantiationIT extends AbstractIntegrationTest {

    protected PbcsApplication application;

    public static final List<String> DIMENSIONS = Arrays.asList("Account", "Currency", "Entity", "Period", "Product", "Scenario", "Version", "Year");

    public static final String ATTRIBUTE_DIM_EXAMPLE = "Market Size";

    @Before
    public void setUp() {
        PbcsClient client = new PbcsClientFactory().createClient(connection);
        application = client.getApplication("Vision");
    }

    @Test
    public void whenGetPlanWithValidDimension() {
        PbcsApplication.PlanTypeConfiguration configuration = new PlanTypeConfigurationImpl.Builder("Plan1")
                .skipCheck()
                .dimensions(DIMENSIONS)
                .build();
        PbcsPlanType cube = application.getPlanType(configuration);
        assertThat(cube.getDimensions(), hasSize(DIMENSIONS.size()));
    }

    @Test
    public void whenGetInvalidDimensionFromPlan() {
        PbcsApplication.PlanTypeConfiguration configuration = new PlanTypeConfigurationImpl.Builder("Plan1")
                .skipCheck()
                .dimensions(DIMENSIONS)
                .build();
        PbcsPlanType cube = application.getPlanType(configuration);

        final String badDimension = "BadDimension";
        PbcsInvalidDimensionException exception = assertThrows(PbcsInvalidDimensionException.class, () -> cube.getDimension(badDimension));
        assertThat(exception.getObjectName(), is(badDimension));
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

        PbcsInvalidDimensionException exception = assertThrows(PbcsInvalidDimensionException.class, () -> application.getPlanType(configuration));
        assertThat(exception.getObjectName(), is(badDimension));
    }

    @Test
    public void whenGetPlanWithAttributeDimension() {
        PbcsApplication.PlanTypeConfiguration configuration = new PlanTypeConfigurationImpl.Builder("Plan1")
                .skipCheck()
                .dimensions(DIMENSIONS)
                .dimension(ATTRIBUTE_DIM_EXAMPLE)
                .validateDimensions()
                .build();

        PbcsPlanType plan = application.getPlanType(configuration);
        PbcsDimension dimension = plan.getDimension(ATTRIBUTE_DIM_EXAMPLE);
        assertThat(dimension.getDimensionType(), is(PbcsMemberType.ATTRIBUTE));
    }

}