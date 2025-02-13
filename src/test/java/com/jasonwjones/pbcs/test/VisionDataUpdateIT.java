package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.client.*;
import com.jasonwjones.pbcs.client.impl.PbcsRetrieveOptionsImpl;
import com.jasonwjones.pbcs.client.impl.grid.DataSliceGrid;
import com.jasonwjones.pbcs.client.impl.grid.DataSliceGridPrinter;
import com.jasonwjones.pbcs.util.GridPrinter;
import com.jasonwjones.pbcs.util.TextGridReader;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class VisionDataUpdateIT extends VisionCubeAdhocIT {

    protected PbcsApplication app;

    protected PbcsPlanType plan;

    @Test
    public void update() throws IOException {
        PovGrid<String> grid = new TextGridReader().readPovGridFromFile("grids/simple4.txt");
        GridPrinter.print(grid);

        DataSliceGrid dataSliceGrid = cube.retrieve(grid, new PbcsRetrieveOptionsImpl());
        DataSliceGridPrinter.print(dataSliceGrid);

        PovGrid<String> grid2 = dataSliceGrid.copyOf(cell -> (cell != null) ? cell.getValue() : null);
        DataSliceGrid dataSliceGrid2 = cube.retrieve(grid2, new PbcsRetrieveOptionsImpl());
        DataSliceGridPrinter.print(dataSliceGrid2);
    }

}