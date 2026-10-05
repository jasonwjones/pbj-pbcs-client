package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.client.PbcsPlanType;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

/**
 * The updated cell count an import reports, read through the same template a live import uses.
 */
public class PbcsPlanTypeImplUpdateCountTest {

    private static final List<String> CELL = Arrays.asList("Actual", "FY23", "Final", "USD", "000", "P_000", "Mar", "4110");

    private final StubRestServer server = new StubRestServer();

    /**
     * What a live pod answers a one-cell write with. The count is spelled {@code numUpdatedCells}, and
     * reading only {@code numUpdateCells} dropped it, so every import looked unreported.
     */
    @Test
    public void theCountALivePodSendsIsRead() {
        server.respond("importdataslice", "{\"numAcceptedCells\":1,\"numRejectedCells\":0,\"rejectedCells\":[],\"rejectedCellsWithDetails\":[],\"numUpdatedCells\":1}");

        PbcsPlanType.ImportDataResult result = server.planType().setCell(CELL, "5");

        assertThat(server.endpoints, contains("importdataslice"));
        assertThat(result.isUpdatedCountReported(), is(true));
        assertThat(result.getUpdatedCells(), is(1));
    }

    @Test
    public void aWriteThatChangedNothingSaysSo() {
        server.respond("importdataslice", "{\"numAcceptedCells\":1,\"numRejectedCells\":0,\"rejectedCells\":[],\"rejectedCellsWithDetails\":[],\"numUpdatedCells\":0}");

        PbcsPlanType.ImportDataResult result = server.planType().setCell(CELL, "5");

        assertThat(result.isUpdatedCountReported(), is(true));
        assertThat(result.getUpdatedCells(), is(0));
    }

    @Test
    public void theFirstSpellingIsStillRead() {
        server.respond("importdataslice", "{\"numAcceptedCells\":1,\"numRejectedCells\":0,\"numUpdateCells\":1}");

        PbcsPlanType.ImportDataResult result = server.planType().setCell(CELL, "5");

        assertThat(result.isUpdatedCountReported(), is(true));
        assertThat(result.getUpdatedCells(), is(1));
    }

    @Test
    public void aResponseWithoutACountIsNotReadAsZero() {
        server.respond("importdataslice", "{\"numAcceptedCells\":1,\"numRejectedCells\":0}");

        PbcsPlanType.ImportDataResult result = server.planType().setCell(CELL, "5");

        assertThat(result.isUpdatedCountReported(), is(false));
    }

}
