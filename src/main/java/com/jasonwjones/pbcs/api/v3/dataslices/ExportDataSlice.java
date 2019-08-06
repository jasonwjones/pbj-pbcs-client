package com.jasonwjones.pbcs.api.v3.dataslices;

public class ExportDataSlice {

	private boolean exportPlanningData = false;

	private GridDefinition gridDefinition;

	public ExportDataSlice(GridDefinition gridDefinition) {
		this.gridDefinition = gridDefinition;
	}
	
	public boolean isExportPlanningData() {
		return exportPlanningData;
	}

	/**
	 * Sets the value for <code>exportPlanningData</code>. When set to true,
	 * supporting details and cell notes will be exported. The default is false.
	 * 
	 * @param exportPlanningData true to turn on export planning data, false
	 *            otherwise
	 */
	public void setExportPlanningData(boolean exportPlanningData) {
		this.exportPlanningData = exportPlanningData;
	}

	public GridDefinition getGridDefinition() {
		return gridDefinition;
	}

	public void setGridDefinition(GridDefinition gridDefinition) {
		this.gridDefinition = gridDefinition;
	}

}
