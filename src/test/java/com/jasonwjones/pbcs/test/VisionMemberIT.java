package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.*;
import com.jasonwjones.pbcs.client.impl.PlanTypeConfigurationImpl;
import com.jasonwjones.pbcs.util.ConnectionUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class VisionMemberIT {

    protected PbcsApplication app;

    protected PbcsExplicitDimensionsPlanType cube;

    public static final List<String> DIMENSIONS = Arrays.asList("Account", "Currency", "Entity", "Period", "Product", "Scenario", "Version", "Year");

    @Before
    public void setUp() {
        PbcsPlanningClient client = new PbcsClientFactory().createClient(ConnectionUtils.defaultConnection());
        app = client.getApplication("Vision");

        PlanTypeConfigurationImpl configuration = new PlanTypeConfigurationImpl();
        configuration.setName("Plan1");
        configuration.setSkipCheck(true);
        configuration.setExplicitDimensions(DIMENSIONS);
        cube = (PbcsExplicitDimensionsPlanType) app.getPlanType(configuration);
    }

    @Test
    public void whenGetMaxGeneration() {
        PbcsDimension dimension = cube.getDimension("Period");
        PbcsMember root = dimension.getRoot();
        assertThat(root.getGeneration(), is(1));
        // Period -> YearTotal -> Q1 -> Jan = 4
        assertThat(root.getMaxGeneration(), is(4));
    }

    @Test
    public void whenGetLevel() {
        PbcsDimension dimension = cube.getDimension("Period");
        PbcsMember root = dimension.getRoot();
        assertThat(root.getLevel(), is(3));
    }
}