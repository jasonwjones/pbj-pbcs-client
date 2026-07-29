package com.jasonwjones.pbcs.util;

import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.client.impl.PlanTypeConfigurationImpl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import com.jasonwjones.pbcs.testing.LiveEpmTestSupport;
import com.jasonwjones.pbcs.testing.ReadOnlyIntegrationTest;

import java.util.Arrays;
import java.util.List;

@Category(ReadOnlyIntegrationTest.class)
public class PlanTypeWalkerIT {

    @BeforeClass
    public static void requireLiveEpmCredentials() {
        LiveEpmTestSupport.assumeDefaultConnectionAvailable();
    }

    private PbcsPlanType plan;

    public static final List<String> DIMENSIONS = Arrays.asList("Account", "Currency", "Entity", "Period", "Product", "Scenario", "Version", "Year");

    @Before
    public void setUp() {
        PbcsApplication app = PbcsClientUtils.vision();

        PbcsApplication.PlanTypeConfiguration configuration = new PlanTypeConfigurationImpl.Builder("Plan1")
                .skipCheck()
                .dimensions(DIMENSIONS)
                .build();

        plan = app.getPlanType(configuration);
    }

    @Test
    public void walk() {
        PlanTypeWalker.Options options = new PlanTypeWalker.Options();
        options.setThreads(1);
        options.setDimensionNames(Arrays.asList("Account", "PeriodX"));
        PlanTypeWalker.walk(plan, new PrinterVisitor(), options);
    }

}
