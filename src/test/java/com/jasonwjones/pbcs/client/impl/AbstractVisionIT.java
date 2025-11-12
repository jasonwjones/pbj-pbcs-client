package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsPlanningClient;
import com.jasonwjones.pbcs.util.ConnectionUtils;
import org.junit.Before;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractVisionIT {

    public static final List<String> DIMENSIONS = Arrays.asList("Account", "Currency", "Entity", "Period", "Product", "Scenario", "Version", "Year");

    public static final String PLAN = "Plan1";

    protected PbcsApplication app;

    protected PlanTypeConfigurationImpl planTypeConfiguration;

    @Before
    public void setUp() {
        PbcsPlanningClient client = new PbcsClientFactory().createClient(ConnectionUtils.defaultConnection());
        app = client.getApplication("Vision");

        planTypeConfiguration = new PlanTypeConfigurationImpl();
        planTypeConfiguration.setName(PLAN);
        planTypeConfiguration.setSkipCheck(true);
        planTypeConfiguration.setExplicitDimensions(DIMENSIONS);
    }

}