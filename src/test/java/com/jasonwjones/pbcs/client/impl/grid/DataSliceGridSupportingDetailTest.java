package com.jasonwjones.pbcs.client.impl.grid;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jasonwjones.pbcs.api.v3.dataslices.DataSlice;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

public class DataSliceGridSupportingDetailTest {

    private static final String SLICE = """
            {"pov":["Actual","FY23","Final","USD","000","P_000"],"columns":[["Mar","Apr"]],"rows":[
              {"headers":["4110"],"data":["400",""],"supportingDetail":[{"items":[
                {"label":"Gas","operator":"+","value":"300","position":0,"generation":0},
                {"label":"Water","operator":"+","value":"100","position":1,"generation":0}
              ]},null]},
              {"headers":["4120"],"data":["",""]}
            ]}
            """;

    @Test
    public void dataCellsCarryTheirSupportingDetail() throws Exception {
        List<DataSlice.SupportingDetail> lines = dataCell(grid(), 1, 1).getSupportingDetail();

        assertThat(lines.stream().map(DataSlice.SupportingDetail::getLabel).toList(), contains("Gas", "Water"));
        assertThat(lines.get(1).getValue(), is("100"));
        assertThat(lines.get(1).getPosition(), is(1));
    }

    @Test
    public void aCellWithoutSupportingDetailHasAnEmptyList() throws Exception {
        DataSliceGrid grid = grid();

        assertThat("a null entry in the row's detail", dataCell(grid, 1, 2).getSupportingDetail(), is(empty()));
        assertThat("a row with no detail list", dataCell(grid, 2, 1).getSupportingDetail(), is(empty()));
    }

    @Test
    public void changingALineDoesNotChangeTheSlice() throws Exception {
        DataSliceGrid grid = grid();

        dataCell(grid, 1, 1).getSupportingDetail().get(0).setValue("1");

        assertThat(dataCell(grid, 1, 1).getSupportingDetail().get(0).getValue(), is("300"));
    }

    private static DataSliceGrid grid() throws Exception {
        return new DataSliceGrid(null, new ObjectMapper().readValue(SLICE, DataSlice.class));
    }

    private static DataSliceGrid.DataCell dataCell(DataSliceGrid grid, int row, int column) {
        return (DataSliceGrid.DataCell) grid.getCell(row, column);
    }

}
