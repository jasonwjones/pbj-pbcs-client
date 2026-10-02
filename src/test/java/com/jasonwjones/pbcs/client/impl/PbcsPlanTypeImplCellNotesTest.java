package com.jasonwjones.pbcs.client.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

/**
 * Cell note reads and writes against canned responses, in the shapes a live pod returned.
 */
public class PbcsPlanTypeImplCellNotesTest {

    private static final List<String> CELL = Arrays.asList("Actual", "FY23", "Final", "USD", "000", "P_000", "Mar", "4110");

    // What a live pod answers a notes-only import with: nothing accepted, nothing updated.
    private static final String IMPORT_RESPONSE = "{\"numAcceptedCells\":0,\"numRejectedCells\":0,\"rejectedCells\":[],\"rejectedCellsWithDetails\":[],\"numUpdatedCells\":0}";

    private static final String EXPORT_WITH_NOTES = "{\"pov\":[\"Actual\",\"FY23\",\"Final\",\"USD\",\"000\",\"P_000\"],\"columns\":[[\"Mar\"]],"
            + "\"rows\":[{\"headers\":[\"4110\"],\"data\":[\"\"],\"cellNotes\":[[{\"contents\":\"first\"},{\"contents\":\"second\"}]]}]}";

    private static final String EXPORT_WITHOUT_NOTES = "{\"pov\":[\"Actual\",\"FY23\",\"Final\",\"USD\",\"000\",\"P_000\"],\"columns\":[[\"Mar\"]],"
            + "\"rows\":[{\"headers\":[\"4110\"],\"data\":[\"\"]}]}";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final StubRestServer server = new StubRestServer();

    @Test
    public void setCellNotesOverwritesWithABlankValueAndReadsTheCellBack() throws Exception {
        server.respond("importdataslice", IMPORT_RESPONSE);
        server.respond("exportdataslice", EXPORT_WITH_NOTES);

        List<String> notes = server.planType().setCellNotes(CELL, Arrays.asList("first", "second"));

        assertThat(notes, contains("first", "second"));
        assertThat(server.endpoints, contains("importdataslice", "exportdataslice"));

        JsonNode importRequest = objectMapper.readTree(server.bodies.get(0));
        assertThat(importRequest.get("cellNotesOption").asText(), is("Overwrite"));
        JsonNode row = importRequest.get("dataGrid").get("rows").get(0);
        assertThat("a blank value leaves the cell's value alone", row.get("data").toString(), is("[\"\"]"));
        assertThat(row.get("cellNotes").toString(), is("[[{\"contents\":\"first\"},{\"contents\":\"second\"}]]"));

        JsonNode exportRequest = objectMapper.readTree(server.bodies.get(1));
        assertThat(exportRequest.get("exportPlanningData").asBoolean(), is(true));
    }

    @Test
    public void appendIsSentAsAppend() throws Exception {
        server.respond("importdataslice", IMPORT_RESPONSE);
        server.respond("exportdataslice", EXPORT_WITH_NOTES);

        server.planType().setCellNotes(CELL, List.of("second"), PbcsPlanType.CellNotesOption.APPEND);

        assertThat(objectMapper.readTree(server.bodies.get(0)).get("cellNotesOption").asText(), is("Append"));
    }

    @Test
    public void skipIsRefusedBeforeAnythingIsSent() {
        try {
            server.planType().setCellNotes(CELL, List.of("ignored"), PbcsPlanType.CellNotesOption.SKIP);
            throw new AssertionError("expected an IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertThat(server.endpoints, is(empty()));
        }
    }

    @Test
    public void aCellWithoutNotesReadsAsEmpty() {
        server.respond("exportdataslice", EXPORT_WITHOUT_NOTES);

        assertThat(server.planType().getCellNotes(CELL), is(empty()));
    }

    @Test
    public void aCellTheExportLeavesOutReadsAsEmpty() {
        server.respond("exportdataslice", "{\"pov\":[\"Actual\",\"FY23\",\"Final\",\"USD\",\"000\",\"P_000\"],\"columns\":[[\"Mar\"]],\"rows\":[]}");

        assertThat(server.planType().getCellNotes(CELL), is(empty()));
    }

}
