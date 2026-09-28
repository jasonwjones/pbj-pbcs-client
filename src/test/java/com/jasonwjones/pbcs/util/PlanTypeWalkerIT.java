package com.jasonwjones.pbcs.util;

import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.client.impl.PlanTypeConfigurationImpl;
import org.junit.Before;
import org.junit.BeforeClass;
import com.jasonwjones.pbcs.client.exceptions.PbcsInvalidDimensionException;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import com.jasonwjones.pbcs.testing.LiveEpmTestSupport;
import com.jasonwjones.pbcs.testing.ReadOnlyIntegrationTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

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
        options.setDimensionNames(Arrays.asList("Account", "Period"));
        PlanTypeWalker.walk(plan, new PrinterVisitor(), options);
    }

    /**
     * A name the plan does not have is refused, rather than skipped over.
     *
     * <p>This test used to be the one above: {@code walk} asked for "PeriodX" and simply blew up, which
     * left it unclear whether an absent dimension was being probed on purpose or misspelt by accident.
     * Asserting the refusal settles it either way - the behaviour is now pinned, and {@code walk} walks
     * dimensions the plan actually has.
     */
    @Test
    public void walkingADimensionThePlanDoesNotHaveIsRefused() {
        PlanTypeWalker.Options options = new PlanTypeWalker.Options();
        options.setThreads(1);
        options.setDimensionNames(Arrays.asList("Account", "PeriodX"));

        PbcsInvalidDimensionException refused = assertThrows(PbcsInvalidDimensionException.class,
                () -> PlanTypeWalker.walk(plan, new PrinterVisitor(), options));

        assertTrue(refused.getMessage(), refused.getMessage().contains("PeriodX"));
    }

}
