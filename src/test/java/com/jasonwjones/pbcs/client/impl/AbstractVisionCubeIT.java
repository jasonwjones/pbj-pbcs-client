package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsExplicitDimensionsPlanType;
import com.jasonwjones.pbcs.client.PbcsPlanningClient;
import com.jasonwjones.pbcs.util.ConnectionUtils;
import org.junit.Before;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractVisionCubeIT extends AbstractVisionIT {

    public static final List<String> DIMENSIONS = Arrays.asList("Account", "Currency", "Entity", "Period", "Product", "Scenario", "Version", "Year");

    public static final List<String> LEVEL0_TEST_CELL = Arrays.asList("Actual", "FY21", "Final", "USD", "000", "P_000", "Jan", "4110");

    /**
     * Nearly identical to the LEVEL0 test cell, except we have an upper-level account (7001)
     */
    public static final List<String> UPPER_LEVEL_TEST_CELL = Arrays.asList("Actual", "FY21", "Final", "USD", "000", "P_000", "Jan", "7001");

    public static final String CELL_TEST_VALUE = "2";

    protected PbcsApplication app;

    protected PbcsExplicitDimensionsPlanType cube;

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

}