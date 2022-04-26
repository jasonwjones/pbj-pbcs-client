package com.jasonwjones.pbcs.client;

import com.jasonwjones.pbcs.client.impl.grid.DataSliceGrid;

import java.util.List;

public interface PbcsPlanType {

	String getName();

	List<PbcsDimension> getDimensions();

	PbcsApplication getApplication();

	String getCell();

	/**
	 * Retrieves a single cell of data at the given POV.
	 *
	 * @param dataPoint the data point
	 * @return the value of the cell, may be an empty string
	 */
	String getCell(List<String> dataPoint);

	DataSliceGrid retrieve(List<String> dataPoint);

}