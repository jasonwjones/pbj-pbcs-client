package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.client.PbcsPlanType;

import java.util.StringJoiner;

/**
 * Default, mutable {@link PbcsPlanType.RetrieveOptions} implementation.
 */
public class PbcsRetrieveOptionsImpl implements PbcsPlanType.RetrieveOptions {

    /**
     * Constructs an instance with default options.
     */
    public PbcsRetrieveOptionsImpl() {
    }

    /**
     * The default maximum. This technically represents the maximum number of cells that can be retrieved from EPM cloud
     * in a single request, although it's possible that with suppress missing turned on, you can request more than this
     * but get many fewer cells. We have no way of knowing what the resulting cell count will be, so this maximum
     * applies to the requests that are made. In other words, you could potentially be breaking up your request into
     * multiple requests unnecessarily.
     */
    public static final int DEFAULT_MAX_CELLS_PER_RETRIEVE = 500000;

    private boolean provideDimensionHints;

    private boolean exportPlanningData;

    private boolean suppressMissingRows;

    private boolean suppressMissingColumns;

    private int maxCellsPerRetrieve = DEFAULT_MAX_CELLS_PER_RETRIEVE;

    @Override
    public boolean isProvideDimensionHints() {
        return provideDimensionHints;
    }

    /**
     * Sets whether dimension hints should be provided on the export call.
     *
     * @param provideDimensionHints true to provide dimension hints, false otherwise
     */
    public void setProvideDimensionHints(boolean provideDimensionHints) {
        this.provideDimensionHints = provideDimensionHints;
    }

    @Override
    public boolean isExportPlanningData() {
        return exportPlanningData;
    }

    /**
     * Sets whether supporting details should be exported along with data.
     *
     * @param exportPlanningData true to export supporting details, false otherwise
     */
    public void setExportPlanningData(boolean exportPlanningData) {
        this.exportPlanningData = exportPlanningData;
    }

    @Override
    public boolean isSuppressMissingRows() {
        return suppressMissingRows;
    }

    /**
     * Sets whether missing rows should be suppressed.
     *
     * @param suppressMissingRows true to suppress missing rows, false otherwise
     */
    public void setSuppressMissing(boolean suppressMissingRows) {
        this.suppressMissingRows = suppressMissingRows;
    }

    @Override
    public boolean isSuppressMissingColumns() {
        return suppressMissingColumns;
    }

    /**
     * Sets whether missing columns should be suppressed.
     *
     * @param suppressMissingColumns true to suppress missing columns, false otherwise
     */
    public void setSuppressMissingColumns(boolean suppressMissingColumns) {
        this.suppressMissingColumns = suppressMissingColumns;
    }

    @Override
    public int getMaxCellsPerRetrieve() {
        return maxCellsPerRetrieve;
    }

    /**
     * Sets the max number of cells to allow per retrieve.
     *
     * @param maxCellsPerRetrieve the max cells per retrieve
     */
    public void setMaxCellsPerRetrieve(int maxCellsPerRetrieve) {
        this.maxCellsPerRetrieve = maxCellsPerRetrieve;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PbcsRetrieveOptionsImpl.class.getSimpleName() + "[", "]")
                .add("exportPlanningData=" + exportPlanningData)
                .add("maxCellsPerRetrieve=" + maxCellsPerRetrieve)
                .add("provideDimensionHints=" + provideDimensionHints)
                .add("suppressMissingColumns=" + suppressMissingColumns)
                .add("suppressMissingRows=" + suppressMissingRows)
                .toString();
    }

}