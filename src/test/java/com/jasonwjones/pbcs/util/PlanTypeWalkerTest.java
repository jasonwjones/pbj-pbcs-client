package com.jasonwjones.pbcs.util;

import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.client.impl.PlanTypeConfigurationImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class PlanTypeWalkerTest {

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
        PlanTypeWalker.walk(plan, new PlanTypeWalker.PrinterVisitor());
    }

}