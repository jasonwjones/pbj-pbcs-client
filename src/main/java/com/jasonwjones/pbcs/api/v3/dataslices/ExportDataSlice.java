package com.jasonwjones.pbcs.api.v3.dataslices;

import java.util.StringJoiner;

/**
 * Represents the request payload for the "export data slice" REST endpoint.
 */
public class ExportDataSlice {

	private boolean exportPlanningData = false;

	private GridDefinition gridDefinition;

	/**
	 * Constructs an instance for the given grid definition.
	 *
	 * @param gridDefinition the grid definition to export
	 */
	public ExportDataSlice(GridDefinition gridDefinition) {
		this.gridDefinition = gridDefinition;
	}

	/**
	 * Gets the value for <code>exportPlanningData</code>.
	 *
	 * @return true if supporting details and cell notes should be exported, false otherwise
	 */
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

	/**
	 * Gets the grid definition to export.
	 *
	 * @return the grid definition
	 */
	public GridDefinition getGridDefinition() {
		return gridDefinition;
	}

	/**
	 * Sets the grid definition to export.
	 *
	 * @param gridDefinition the grid definition
	 */
	public void setGridDefinition(GridDefinition gridDefinition) {
		this.gridDefinition = gridDefinition;
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", ExportDataSlice.class.getSimpleName() + "[", "]")
				.add("exportPlanningData=" + exportPlanningData)
				.add("gridDefinition=" + gridDefinition)
				.toString();
	}

}
