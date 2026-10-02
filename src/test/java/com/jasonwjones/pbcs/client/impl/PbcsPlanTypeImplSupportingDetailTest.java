package com.jasonwjones.pbcs.client.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jasonwjones.pbcs.api.v3.dataslices.DataSlice;
import com.jasonwjones.pbcs.client.exceptions.PbcsDataImportException;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

/**
 * Supporting detail reads and writes against canned responses, in the shapes a live pod returned.
 */
public class PbcsPlanTypeImplSupportingDetailTest {

    private static final List<String> CELL = Arrays.asList("Actual", "FY23", "Final", "USD", "000", "P_000", "Mar", "4110");

    private static final String ACCEPTED = "{\"numAcceptedCells\":1,\"numRejectedCells\":0,\"rejectedCells\":[],\"rejectedCellsWithDetails\":[],\"numUpdatedCells\":1}";

    // What a live pod answered supporting detail on 4001, the stored level-1 parent of 4110, with.
    private static final String REJECTED = "{\"numAcceptedCells\":0,\"numRejectedCells\":1,\"rejectedCells\":[\"[Actual, FY23, Final, USD, 000, P_000, Mar, 4001]\"],"
            + "\"rejectedCellsWithDetails\":[{\"memberNames\":[\"Actual\",\"FY23\",\"Final\",\"USD\",\"000\",\"P_000\",\"Mar\",\"4001\"],\"readOnlyReasons\":[\"Upper Level Bottom Up Cell\"],\"otherReasons\":[]}],\"numUpdatedCells\":0}";

    private static final String EXPORT_WITH_DETAIL = "{\"pov\":[\"Actual\",\"FY23\",\"Final\",\"USD\",\"000\",\"P_000\"],\"columns\":[[\"Mar\"]],"
            + "\"rows\":[{\"headers\":[\"4110\"],\"data\":[\"400\"],\"supportingDetail\":[{\"items\":["
            + "{\"label\":\"Gas\",\"operator\":\"+\",\"value\":\"300\",\"position\":0,\"generation\":0},"
            + "{\"label\":\"Water\",\"operator\":\"+\",\"value\":\"100\",\"position\":1,\"generation\":0}]}]}]}";

    private static final String EXPORT_WITHOUT_DETAIL = "{\"pov\":[\"Actual\",\"FY23\",\"Final\",\"USD\",\"000\",\"P_000\"],\"columns\":[[\"Mar\"]],"
            + "\"rows\":[{\"headers\":[\"4110\"],\"data\":[\"400\"]}]}";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final StubRestServer server = new StubRestServer();

    /** The server stores lines only beside a non-blank value, and never works the value out itself. */
    @Test
    public void theLinesAreSentWithTheirTotal() throws Exception {
        server.respond("importdataslice", ACCEPTED);
        server.respond("exportdataslice", EXPORT_WITH_DETAIL);

        List<DataSlice.SupportingDetail> lines = server.planType().setSupportingDetail(CELL, Arrays.asList(
                new DataSlice.SupportingDetail("Gas", "+", "300"),
                new DataSlice.SupportingDetail("Water", "+", "100")));

        assertThat(lines.stream().map(DataSlice.SupportingDetail::getLabel).toList(), contains("Gas", "Water"));
        assertThat(server.endpoints, contains("importdataslice", "exportdataslice"));
        JsonNode row = importRow();
        assertThat(row.get("data").toString(), is("[\"400\"]"));
        assertThat(row.get("supportingDetail").toString(), is("[{\"items\":["
                + "{\"label\":\"Gas\",\"operator\":\"+\",\"value\":\"300\",\"position\":0,\"generation\":0},"
                + "{\"label\":\"Water\",\"operator\":\"+\",\"value\":\"100\",\"position\":1,\"generation\":0}]}]"));
        assertThat(objectMapper.readTree(server.bodies.get(1)).get("exportPlanningData").asBoolean(), is(true));
    }

    @Test
    public void aParentLineIsSentWithItsChildrensTotal() throws Exception {
        server.respond("importdataslice", ACCEPTED);
        server.respond("exportdataslice", EXPORT_WITH_DETAIL);

        server.planType().setSupportingDetail(CELL, Arrays.asList(
                new DataSlice.SupportingDetail("Total Postage", "+", null),
                new DataSlice.SupportingDetail("Employee Count", "+", "43", 1),
                new DataSlice.SupportingDetail("Postage Allocation", "*", "102", 1),
                new DataSlice.SupportingDetail("Rebate", "-", "500")));

        JsonNode row = importRow();
        assertThat(row.get("data").toString(), is("[\"3886\"]"));
        assertThat(row.get("supportingDetail").get(0).get("items").get(0).get("value").asText(), is("4386"));
    }

    /** An empty wrapper deletes the detail; the blank value beside it leaves the cell's value alone. */
    @Test
    public void anEmptyListDeletesTheDetailAndLeavesTheValue() throws Exception {
        server.respond("importdataslice", ACCEPTED);
        server.respond("exportdataslice", EXPORT_WITHOUT_DETAIL);

        assertThat(server.planType().setSupportingDetail(CELL, Collections.emptyList()), is(empty()));

        JsonNode row = importRow();
        assertThat(row.get("data").toString(), is("[\"\"]"));
        assertThat(row.get("supportingDetail").toString(), is("[{\"items\":[]}]"));
    }

    @Test(expected = PbcsDataImportException.class)
    public void aRejectedCellThrows() {
        server.respond("importdataslice", REJECTED);

        server.planType().setSupportingDetail(CELL, Collections.singletonList(new DataSlice.SupportingDetail("Gas", "+", "300")));
    }

    /** The server answers a bad operator with a 400 about JSON syntax; better to say what is wrong. */
    @Test
    public void badLinesAreRefusedBeforeAnythingIsSent() {
        refused(new DataSlice.SupportingDetail("Gas", "x", "300"));
        refused(new DataSlice.SupportingDetail("Unit", "+", null), new DataSlice.SupportingDetail("Rate", "*", "250"));
        assertThat(server.endpoints, is(empty()));
    }

    @Test
    public void aCellWithoutDetailReadsAsEmpty() {
        server.respond("exportdataslice", EXPORT_WITHOUT_DETAIL);

        assertThat(server.planType().getSupportingDetail(CELL), is(empty()));
    }

    private void refused(DataSlice.SupportingDetail... lines) {
        try {
            server.planType().setSupportingDetail(CELL, Arrays.asList(lines));
            throw new AssertionError("expected an IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // refused
        }
    }

    private JsonNode importRow() throws Exception {
        JsonNode request = objectMapper.readTree(server.bodies.get(0));
        return request.get("dataGrid").get("rows").get(0);
    }

}
