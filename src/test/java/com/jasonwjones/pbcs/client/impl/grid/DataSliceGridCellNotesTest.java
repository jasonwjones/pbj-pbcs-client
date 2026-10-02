package com.jasonwjones.pbcs.client.impl.grid;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jasonwjones.pbcs.api.v3.dataslices.DataSlice;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

public class DataSliceGridCellNotesTest {

    private static final String SLICE = """
            {"pov":["Actual","FY23","Final","USD","000","P_000"],"columns":[["Mar","Apr"]],"rows":[
              {"headers":["4110"],"data":["",""],"cellNotes":[[{"contents":"X"},{"contents":"Y"}],null]},
              {"headers":["4120"],"data":["","123"]}
            ]}
            """;

    @Test
    public void dataCellsCarryTheirNotes() throws Exception {
        DataSliceGrid grid = grid(SLICE);

        assertThat(dataCell(grid, 1, 1).getCellNotes(), contains("X", "Y"));
    }

    @Test
    public void aCellWithoutNotesHasAnEmptyList() throws Exception {
        DataSliceGrid grid = grid(SLICE);

        assertThat("a null entry in the row's notes", dataCell(grid, 1, 2).getCellNotes(), is(empty()));
        assertThat("a row with no notes list", dataCell(grid, 2, 2).getCellNotes(), is(empty()));
        assertThat(dataCell(grid, 2, 2).getValue(), is("123"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void notesCannotBeChangedThroughTheGrid() throws Exception {
        dataCell(grid(SLICE), 1, 1).getCellNotes().clear();
    }

    private static DataSliceGrid grid(String json) throws Exception {
        return new DataSliceGrid(null, new ObjectMapper().readValue(json, DataSlice.class));
    }

    private static DataSliceGrid.DataCell dataCell(DataSliceGrid grid, int row, int column) {
        DataSliceGrid.Cell cell = grid.getCell(row, column);
        assertThat(cell.getType(), is(DataSliceGrid.CellType.DATA));
        return (DataSliceGrid.DataCell) cell;
    }

}
