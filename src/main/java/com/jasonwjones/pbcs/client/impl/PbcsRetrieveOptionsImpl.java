package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.client.PbcsPlanType;

public class PbcsRetrieveOptionsImpl implements PbcsPlanType.RetrieveOptions {

    private boolean provideDimensionHints;

    private boolean exportPlanningData;

    private boolean suppressMissing;

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
        return suppressMissing;
    }

    public void setSuppressMissing(boolean suppressMissing) {
        this.suppressMissing = suppressMissing;
    }

}