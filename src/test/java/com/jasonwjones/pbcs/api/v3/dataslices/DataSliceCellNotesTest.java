package com.jasonwjones.pbcs.api.v3.dataslices;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

/**
 * Cell notes on the wire, in the shapes a live pod sent and accepted.
 */
public class DataSliceCellNotesTest {

    private static final List<String> CELL = Arrays.asList("Actual", "FY23", "Final", "USD", "000", "P_000", "Mar", "4110");

    private final ObjectMapper objectMapper = new ObjectMapper();

    /** An export response captured from a live pod: notes on one row, none on the other. */
    @Test
    public void exportedNotesAreReadPerRowAndCell() throws Exception {
        String json = """
                {"pov":["Actual","FY23","Final","USD","000","P_000"],"columns":[["Mar","Apr"]],"rows":[
                  {"headers":["4110"],"data":["",""],"cellNotes":[[{"contents":"X"},{"contents":"Y"}],[]]},
                  {"headers":["4120"],"data":["",""]}
                ]}
                """;

        DataSlice slice = objectMapper.readValue(json, DataSlice.class);

        List<List<DataSlice.CellNote>> notes = slice.getRows().get(0).getCellNotes();
        assertThat(notes.get(0), contains(new DataSlice.CellNote("X"), new DataSlice.CellNote("Y")));
        assertThat(notes.get(1), is(empty()));
        assertThat("a row without notes has no list at all", slice.getRows().get(1).getCellNotes(), is(nullValue()));
    }

    /** An import that carries no notes must send exactly the payload it always has. */
    @Test
    public void anImportWithoutNotesSendsNoCellNotesField() throws Exception {
        JsonNode row = rowJson(new ImportDataSlice(CELL, "5"));

        assertThat(row.has("cellNotes"), is(false));
    }

    /** A null element is how one cell says "leave my notes alone" under Overwrite, so it must survive. */
    @Test
    public void nullCellsInsideTheNotesListAreSent() throws Exception {
        ImportDataSlice importDataSlice = new ImportDataSlice(CELL, "");
        importDataSlice.getDataGrid().getRows().get(0).setCellNotes(Arrays.asList(null, Collections.singletonList(new DataSlice.CellNote("apr only"))));

        JsonNode cellNotes = rowJson(importDataSlice).get("cellNotes");

        assertThat(cellNotes.toString(), is("[null,[{\"contents\":\"apr only\"}]]"));
    }

    /** The import endpoint fails the whole request over any note field it does not recognize. */
    @Test
    public void aNoteSerializesItsContentsOnly() throws Exception {
        String json = objectMapper.writeValueAsString(new DataSlice.CellNote("line one\nline two <tag> & é"));

        assertThat(json, is("{\"contents\":\"line one\\nline two <tag> & é\"}"));
    }

    private JsonNode rowJson(ImportDataSlice importDataSlice) throws Exception {
        return objectMapper.readTree(objectMapper.writeValueAsString(importDataSlice)).get("dataGrid").get("rows").get(0);
    }

}
