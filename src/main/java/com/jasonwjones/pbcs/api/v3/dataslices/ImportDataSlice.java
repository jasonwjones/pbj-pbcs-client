package com.jasonwjones.pbcs.api.v3.dataslices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImportDataSlice {

    private boolean aggregateEssbaseData = false;

    private String cellNotesOption = "Skip";

    private String dateFormat = "DD/MM/YYYY";

    private boolean dryRun = false;

    private boolean strictDateValidation = true;

    private Map<String, Object> customParams;

    private DataSlice dataGrid;

    public ImportDataSlice() {
        customParams = new HashMap<>();
        customParams.put("IncludeRejectedCells", "true");
        customParams.put("IncludeRejectedCellsWithDetails", "true");
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

    public Map<String, Object> getCustomParams() {
        return customParams;
    }

    public void setCustomParams(Map<String, Object> customParams) {
        this.customParams = customParams;
    }

    public DataSlice getDataGrid() {
        return dataGrid;
    }

    public void setDataGrid(DataSlice dataGrid) {
        this.dataGrid = dataGrid;
    }

}