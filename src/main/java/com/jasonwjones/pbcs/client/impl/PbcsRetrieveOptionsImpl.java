package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.client.PbcsPlanType;

public class PbcsRetrieveOptionsImpl implements PbcsPlanType.RetrieveOptions {

    private boolean provideDimensionHints;

    private boolean exportPlanningData;

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

}