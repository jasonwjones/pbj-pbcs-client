package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.client.exceptions.PbcsDataImportException;
import com.jasonwjones.pbcs.client.exceptions.PbcsInvalidDimensionException;
import com.jasonwjones.pbcs.client.impl.PbcsPlanTypeImpl;
import com.jasonwjones.pbcs.client.impl.PlanTypeConfigurationImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;

public class VisionIT extends AbstractIntegrationTest {

    public static final List<String> DIMENSIONS = Arrays.asList("Account", "Currency", "Entity", "Period", "Product", "Scenario", "Version", "Year");

    public static final List<String> INVALID_DIMENSIONS = Arrays.asList("Invalid1", "Invalid2");

    protected PbcsApplication app;

    @Before
    public void setUp() {
        PbcsClient client = new PbcsClientFactory().createClient(connection);
        app = client.getApplication("Vision");
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

}