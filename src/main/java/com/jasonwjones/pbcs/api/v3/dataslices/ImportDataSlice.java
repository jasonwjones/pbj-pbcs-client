package com.jasonwjones.pbcs.api.v3.dataslices;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ImportDataSlice {

    private boolean aggregateEssbaseData = false;

    // possible values: Overwrite, Append, Skip
    private String cellNotesOption = "Skip";

    // additional notes: https://docs.oracle.com/en/cloud/saas/enterprise-performance-management-common/prest/import_dataslices.html
    private String dateFormat = "DD/MM/YYYY";

    private boolean dryRun = false;

    private boolean strictDateValidation = true;

    private CustomParams customParams;

    private DataSlice dataGrid;

    public ImportDataSlice() {
        customParams = new CustomParams(true, true);
    }

    public ImportDataSlice(List<String> pov, String value) {
        this();
        dataGrid = new DataSlice(pov, value);
    }

    public boolean isAggregateEssbaseData() {
        return aggregateEssbaseData;
    }

    public void setAggregateEssbaseData(boolean aggregateEssbaseData) {
        this.aggregateEssbaseData = aggregateEssbaseData;
    }

    public String getCellNotesOption() {
        return cellNotesOption;
    }

    public void setCellNotesOption(String cellNotesOption) {
        this.cellNotesOption = cellNotesOption;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public boolean isDryRun() {
        return dryRun;
    }

    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }

    public boolean isStrictDateValidation() {
        return strictDateValidation;
    }

    public void setStrictDateValidation(boolean strictDateValidation) {
        this.strictDateValidation = strictDateValidation;
    }

    public CustomParams getCustomParams() {
        return customParams;
    }

    public void setCustomParams(CustomParams customParams) {
        this.customParams = customParams;
    }

    public DataSlice getDataGrid() {
        return dataGrid;
    }

    public void setDataGrid(DataSlice dataGrid) {
        this.dataGrid = dataGrid;
    }

    public static class CustomParams {

        @JsonProperty("PostDataImportRuleNames")
        private String postDataImportRuleNames;

        @JsonProperty("IncludeRejectedCells")
        private boolean includeRejectedCells;

        @JsonProperty("includeRejectedCellsWithDetails")
        private boolean includeRejectedCellsWithDetails;

        public CustomParams() {}

        public CustomParams(boolean includeRejectedCells, boolean includeRejectedCellsWithDetails) {
            this.includeRejectedCells = includeRejectedCells;
            this.includeRejectedCellsWithDetails = includeRejectedCellsWithDetails;
        }

        public String getPostDataImportRuleNames() {
            return postDataImportRuleNames;
        }

        public void setPostDataImportRuleNames(String postDataImportRuleNames) {
            this.postDataImportRuleNames = postDataImportRuleNames;
        }

        public boolean isIncludeRejectedCells() {
            return includeRejectedCells;
        }

        public void setIncludeRejectedCells(boolean includeRejectedCells) {
            this.includeRejectedCells = includeRejectedCells;
        }

        public boolean isIncludeRejectedCellsWithDetails() {
            return includeRejectedCellsWithDetails;
        }

        public void setIncludeRejectedCellsWithDetails(boolean includeRejectedCellsWithDetails) {
            this.includeRejectedCellsWithDetails = includeRejectedCellsWithDetails;
        }

    }

}