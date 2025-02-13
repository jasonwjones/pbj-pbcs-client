package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.client.PbcsPlanType;

public class PbcsRetrieveOptionsImpl implements PbcsPlanType.RetrieveOptions {

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

    private int maxCellsPerRetrieve = DEFAULT_MAX_CELLS_PER_RETRIEVE;

    @Override
    public boolean isProvideDimensionHints() {
        return provideDimensionHints;
    }

    public void setProvideDimensionHints(boolean provideDimensionHints) {
        this.provideDimensionHints = provideDimensionHints;
    }

    @Override
    public boolean isExportPlanningData() {
        return exportPlanningData;
    }

    public void setExportPlanningData(boolean exportPlanningData) {
        this.exportPlanningData = exportPlanningData;
    }

    @Override
    public boolean isSuppressMissingRows() {
        return suppressMissingRows;
    }

    public void setSuppressMissing(boolean suppressMissingRows) {
        this.suppressMissingRows = suppressMissingRows;
    }

    @Override
    public int getMaxCellsPerRetrieve() {
        return maxCellsPerRetrieve;
    }

    public void setMaxCellsPerRetrieve(int maxCellsPerRetrieve) {
        this.maxCellsPerRetrieve = maxCellsPerRetrieve;
    }

}