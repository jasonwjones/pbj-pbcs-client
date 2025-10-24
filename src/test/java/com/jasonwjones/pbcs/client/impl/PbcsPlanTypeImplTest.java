package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.client.exceptions.PbcsGeneralException;
import com.jasonwjones.pbcs.client.impl.grid.DataSliceGrid;
import com.jasonwjones.pbcs.test.VisionCubeIT;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThrows;

public class PbcsPlanTypeImplTest extends VisionCubeIT {

    @Test
    public void whenRetrieveNormal() {
        DataSliceGrid grid = cube.retrieve(Arrays.asList("Actual", "Final", "P_000", "FY21", "4110", "USD", "000", "IDescendants(Period)"));
        grid.print();
        assertThat(grid.getPov(), hasSize(6)); // not a great test but good enough for now
    }

    @Test
    public void whenRetrieveWithInvalidIntersection() {
        List<String> badPov = Arrays.asList("XActual", "Final", "P_000", "FY21", "4110", "USD", "000", "IDescendants(Period)");
        assertThrows(PbcsGeneralException.class, () -> cube.retrieve(badPov));
    }

}