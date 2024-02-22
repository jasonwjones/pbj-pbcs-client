package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.Grid;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.client.PovGrid;
import com.jasonwjones.pbcs.client.exceptions.PbcsDataImportException;
import com.jasonwjones.pbcs.client.impl.PbcsPlanTypeImpl;
import com.jasonwjones.pbcs.client.impl.PlanTypeConfigurationImpl;
import com.jasonwjones.pbcs.client.impl.PovGridImpl;
import com.jasonwjones.pbcs.client.impl.grid.DataSliceGrid;
import com.jasonwjones.pbcs.client.impl.grid.DataSliceGridPrinter;
import com.jasonwjones.pbcs.util.GridDefinitionPrinter;
import com.jasonwjones.pbcs.util.GridPrinter;
import com.jasonwjones.pbcs.util.GridUtils;
import com.jasonwjones.pbcs.util.TextGridReader;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.jasonwjones.pbcs.matchers.DataSliceGridMatcher.hasDimensions;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class VisionCubeAdhocIT extends AbstractIntegrationTest {

    protected PbcsPlanType cube;

    public static final List<String> DIMENSIONS = Arrays.asList("Account", "Currency", "Entity", "Period", "Product", "Scenario", "Version", "Year", "Market Size");

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

    @Test
    public void getCell() throws IOException {
        Grid<String> grid = new TextGridReader().read("grids/simple4.txt");
        List<String> pov = new ArrayList<>(GridUtils.nonNullRowItems(grid, 0));
        Grid<String> actualGrid = GridUtils.subgrid(grid, 1);

        GridPrinter.print(grid);

        DataSliceGrid dataSliceGrid = cube.retrieve(pov, actualGrid);
        DataSliceGridPrinter.print(dataSliceGrid);
    }

    @Test
    public void retrieveWithAttributes() throws IOException {
        Grid<String> grid = new TextGridReader().read("grids/simple-attribute2.txt");
        List<String> pov = new ArrayList<>(GridUtils.nonNullRowItems(grid, 0));
        Grid<String> actualGrid = GridUtils.subgrid(grid, 1);

        GridPrinter.print(grid);

        DataSliceGrid dataSliceGrid = cube.retrieve(pov, actualGrid);
        DataSliceGridPrinter.print(dataSliceGrid);
    }

    @Test
    // -Dhttps.proxyHost=localhost -Dhttps.proxyPort=8080
    public void retrieveWithAttributesUseHinting() {
        PovGrid<String> grid = grid("grids/simple-attribute2.txt");
        DataSliceGrid dataSliceGrid = retrieve(grid);
        // can't use until we read a PovGrid directly and chop the POV
        //assertThat(dataSliceGrid, hasDimensions(grid));
        assertThat(dataSliceGrid, hasDimensions(3, 3));
    }

    public PovGrid<String> grid(String resourceName) {
        try {
            Grid<String> grid = new TextGridReader().read(resourceName);
            List<String> pov = new ArrayList<>(GridUtils.nonNullRowItems(grid, 0));
            Grid<String> actualGrid = GridUtils.subgrid(grid, 1);
            return new PovGridImpl<>(pov, actualGrid);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read grid from " + resourceName + ": " + e.getMessage());
        }
    }

    private DataSliceGrid retrieve(PovGrid<String> grid) {
        GridPrinter.print(grid);

        DataSliceGrid dataSliceGrid = cube.retrieve(grid, null);
        DataSliceGridPrinter.print(dataSliceGrid);
        return dataSliceGrid;
    }

}