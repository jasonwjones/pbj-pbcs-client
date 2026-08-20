package com.jasonwjones.pbcs.api.v3.dataslices;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Represents the request payload for the "import data slice" REST endpoint.
 */
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

    /**
     * Constructs an instance with default options and rejected-cell reporting enabled.
     */
    public ImportDataSlice() {
        customParams = new CustomParams(true, true);
    }

    /**
     * Constructs an instance for a single-cell import at the given POV.
     *
     * @param pov the POV
     * @param value the cell value
     */
    public ImportDataSlice(List<String> pov, String value) {
        this();
        dataGrid = new DataSlice(pov, value);
    }

    /**
     * Whether values should be added to existing values rather than overwriting them.
     *
     * @return true to aggregate, false to overwrite
     */
    public boolean isAggregateEssbaseData() {
        return aggregateEssbaseData;
    }

    /**
     * Sets whether values should be added to existing values rather than overwriting them.
     *
     * @param aggregateEssbaseData true to aggregate, false to overwrite
     */
    public void setAggregateEssbaseData(boolean aggregateEssbaseData) {
        this.aggregateEssbaseData = aggregateEssbaseData;
    }

    /**
     * Gets the cell notes option, one of {@code Overwrite}, {@code Append}, or {@code Skip}.
     *
     * @return the cell notes option
     */
    public String getCellNotesOption() {
        return cellNotesOption;
    }

    /**
     * Sets the cell notes option.
     *
     * @param cellNotesOption the cell notes option
     */
    public void setCellNotesOption(String cellNotesOption) {
        this.cellNotesOption = cellNotesOption;
    }

    /**
     * Gets the date format used to parse date-typed cell values.
     *
     * @return the date format
     */
    public String getDateFormat() {
        return dateFormat;
    }

    /**
     * Sets the date format used to parse date-typed cell values.
     *
     * @param dateFormat the date format
     */
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    /**
     * Whether this is a dry run (i.e., validate without importing).
     *
     * @return true if a dry run, false otherwise
     */
    public boolean isDryRun() {
        return dryRun;
    }

    /**
     * Sets whether this is a dry run.
     *
     * @param dryRun true if a dry run, false otherwise
     */
    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }

    /**
     * Whether strict date validation is enabled.
     *
     * @return true if strict date validation is enabled, false otherwise
     */
    public boolean isStrictDateValidation() {
        return strictDateValidation;
    }

    /**
     * Sets whether strict date validation is enabled.
     *
     * @param strictDateValidation true to enable strict date validation, false otherwise
     */
    public void setStrictDateValidation(boolean strictDateValidation) {
        this.strictDateValidation = strictDateValidation;
    }

    /**
     * Gets the custom parameters for this import.
     *
     * @return the custom parameters
     */
    public CustomParams getCustomParams() {
        return customParams;
    }

    /**
     * Sets the custom parameters for this import.
     *
     * @param customParams the custom parameters
     */
    public void setCustomParams(CustomParams customParams) {
        this.customParams = customParams;
    }

    /**
     * Gets the data to import.
     *
     * @return the data grid
     */
    public DataSlice getDataGrid() {
        return dataGrid;
    }

    /**
     * Sets the data to import.
     *
     * @param dataGrid the data grid
     */
    public void setDataGrid(DataSlice dataGrid) {
        this.dataGrid = dataGrid;
    }

    /**
     * Additional, non-standard parameters accepted by the "import data slice" endpoint, controlling post-import
     * rule execution and how rejected cells are reported.
     */
    public static class CustomParams {

        @JsonProperty("PostDataImportRuleNames")
        private String postDataImportRuleNames;

        @JsonProperty("IncludeRejectedCells")
        private boolean includeRejectedCells;

        @JsonProperty("IncludeRejectedCellsWithDetails")
        private boolean includeRejectedCellsWithDetails;

        /**
         * Constructs an empty instance for deserialization.
         */
        public CustomParams() {}

        /**
         * Constructs an instance with the given rejected-cell reporting options.
         *
         * @param includeRejectedCells whether rejected cells should be included in the response
         * @param includeRejectedCellsWithDetails whether rejected cells should include additional details
         */
        public CustomParams(boolean includeRejectedCells, boolean includeRejectedCellsWithDetails) {
            this.includeRejectedCells = includeRejectedCells;
            this.includeRejectedCellsWithDetails = includeRejectedCellsWithDetails;
        }

        /**
         * Gets the post data import rule names.
         *
         * @return the post data import rule names, normally null
         */
        public String getPostDataImportRuleNames() {
            return postDataImportRuleNames;
        }

        /**
         * Sets the post data import rule names.
         *
         * @param postDataImportRuleNames the post data import rule names
         */
        public void setPostDataImportRuleNames(String postDataImportRuleNames) {
            this.postDataImportRuleNames = postDataImportRuleNames;
        }

        /**
         * Whether rejected cells should be included in the response.
         *
         * @return true if rejected cells should be included, false otherwise
         */
        public boolean isIncludeRejectedCells() {
            return includeRejectedCells;
        }

        /**
         * Sets whether rejected cells should be included in the response.
         *
         * @param includeRejectedCells true to include rejected cells, false otherwise
         */
        public void setIncludeRejectedCells(boolean includeRejectedCells) {
            this.includeRejectedCells = includeRejectedCells;
        }

        /**
         * Whether rejected cells should include additional details.
         *
         * @return true if additional details should be included, false otherwise
         */
        public boolean isIncludeRejectedCellsWithDetails() {
            return includeRejectedCellsWithDetails;
        }

        /**
         * Sets whether rejected cells should include additional details.
         *
         * @param includeRejectedCellsWithDetails true to include additional details, false otherwise
         */
        public void setIncludeRejectedCellsWithDetails(boolean includeRejectedCellsWithDetails) {
            this.includeRejectedCellsWithDetails = includeRejectedCellsWithDetails;
        }

    }

}
