package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.client.*;
import com.jasonwjones.pbcs.client.impl.grid.DataSliceGrid;
import com.jasonwjones.pbcs.client.impl.grid.DataSliceGridPrinter;
import com.jasonwjones.pbcs.util.GridPrinter;
import com.jasonwjones.pbcs.util.TextGridReader;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class VisionDataUpdateIT extends VisionCubeAdhocIT {

    private static final Logger logger = LoggerFactory.getLogger(VisionDataUpdateIT.class);

    protected PbcsApplication app;

    protected PbcsPlanType plan;

    private static final PbcsPlanType.RetrieveOptions DEFAULT_RETRIEVE_OPTIONS = new DefaultRetrieveOptions();

    @Test
    public void update() throws IOException {
        PovGrid<String> grid = new TextGridReader().readPovGridFromFile("grids/simple4.txt");
        GridPrinter.print(grid);

        DataSliceGrid dataSliceGrid = cube.retrieve(grid, DEFAULT_RETRIEVE_OPTIONS);
        DataSliceGridPrinter.print(dataSliceGrid);

        PovGrid<String> grid2 = dataSliceGrid.copyOf(cell -> (cell != null) ? cell.getValue() : null);
        DataSliceGrid dataSliceGrid2 = cube.retrieve(grid2, DEFAULT_RETRIEVE_OPTIONS);
        DataSliceGridPrinter.print(dataSliceGrid2);
    }

    public static class DefaultRetrieveOptions implements PbcsPlanType.RetrieveOptions {

        @Override
        public boolean isProvideDimensionHints() {
            return false;
        }

        @Override
        public boolean isExportPlanningData() {
            return false;
        }

    }

}