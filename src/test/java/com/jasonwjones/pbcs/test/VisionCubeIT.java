package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.client.exceptions.PbcsDataImportException;
import com.jasonwjones.pbcs.client.impl.PbcsPlanTypeImpl;
import com.jasonwjones.pbcs.client.impl.PlanTypeConfigurationImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class VisionCubeIT extends AbstractIntegrationTest {

    private PbcsPlanType cube;

    public static final List<String> DIMENSIONS = Arrays.asList("Account", "Currency", "Entity", "Period", "Product", "Scenario", "Version", "Year");

    public static final List<String> LEVEL0_TEST_CELL = Arrays.asList("Actual", "FY21", "Final", "USD", "000", "P_000", "Jan", "4110");

    /**
     * Nearly identical to the LEVEL0 test cell, except we have an upper-level account (7001)
     */
    public static final List<String> UPPER_LEVEL_TEST_CELL = Arrays.asList("Actual", "FY21", "Final", "USD", "000", "P_000", "Jan", "7001");

    public static final String CELL_TEST_VALUE = "2";

    @Before
    public void setUp() {
        PbcsClient client = new PbcsClientFactory().createClient(connection);
        PbcsApplication app = client.getApplication("Vision");

        PlanTypeConfigurationImpl configuration = new PlanTypeConfigurationImpl();
        configuration.setName("Plan1");
        configuration.setSkipCheck(true);
        configuration.setExplicitDimensions(DIMENSIONS);
        cube = app.getPlanType(configuration);
    }

    /**
     * Since getCell() is just a convenience method for retrieving with all default members, fetching a specific cell
     * with the dimension names should yield the exact same value. Note, however, that this is mostly a smoke test to
     * ensure that no exception is thrown, since both of these retrieves can very easily just return nothing.
     */
    @Test
    public void getCell() {
        assertThat(cube.getCell(), is(cube.getCell(DIMENSIONS)));
    }

    @Test
    public void getDimensions() {
        assertThat(cube.getDimensions().size(), is(DIMENSIONS.size()));
    }

    @Test
    public void setCell() {
        PbcsPlanType.ImportDataResult result = cube.setCell(LEVEL0_TEST_CELL, CELL_TEST_VALUE);
        assertThat(CELL_TEST_VALUE, is(cube.getCell(LEVEL0_TEST_CELL)));
        assertThat(1, is(result.getAcceptedCells()));
    }

    @Test
    public void setCellUpperLevel() {
        PbcsPlanType.ImportDataResult result = cube.setCell(UPPER_LEVEL_TEST_CELL, CELL_TEST_VALUE);
        assertThat(1, is(result.getRejectedCells()));
        assertThat(0, is(result.getAcceptedCells()));
    }

    @Test(expected = PbcsDataImportException.class)
    public void rejectedCellsThrowsException() {
        PbcsPlanTypeImpl.ImportDataOptionsImpl options = new PbcsPlanTypeImpl.ImportDataOptionsImpl();
        options.setThrowExceptionIfAnyRejectedCells(true);
        cube.setCell(UPPER_LEVEL_TEST_CELL, CELL_TEST_VALUE, options);
    }

    @Test
    public void setCellDryRun() {
        cube.setCell(LEVEL0_TEST_CELL, CELL_TEST_VALUE);

        PbcsPlanTypeImpl.ImportDataOptionsImpl options = new PbcsPlanTypeImpl.ImportDataOptionsImpl();
        options.setDryRun(true);
        cube.setCell(LEVEL0_TEST_CELL, "#Missing", options);

        // expecting that setting missing did not blank the cell since dry run is on
        assertThat(cube.getCell(LEVEL0_TEST_CELL), is(CELL_TEST_VALUE));
    }

}