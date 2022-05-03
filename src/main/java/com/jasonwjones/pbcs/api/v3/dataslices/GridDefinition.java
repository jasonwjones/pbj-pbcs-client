package com.jasonwjones.pbcs.api.v3.dataslices;

import java.util.Arrays;
import java.util.List;

/**
 * POJO used to model grid for Export Data Slice operation on PBCS REST API.
 * Should be kept as clean as possible (i.e. avoid temptation to add convenience
 * methods).
 *
 * @author jasonwjones
 *
 */
public class GridDefinition {

	private boolean suppressMissingBlocks = false;

	private DimensionMembers pov;

	private List<DimensionMembers> columns;

	private List<DimensionMembers> rows;

	public GridDefinition() {}

//	public GridDefinition(List<String> pov, List<List<String>> columns, List<List<String>> rows) {
//		this.pov = new DimensionMembers(pov);
//		this.columns = Arrays.asList(DimensionMembers.of(columns));
//		this.rows = Arrays.asList(DimensionMembers.of(rows));
//	}

	public GridDefinition(List<String> pov, List<DimensionMembers> columns, List<DimensionMembers> rows) {
		this.pov = DimensionMembers.ofMemberNames(pov);
		this.columns = columns;
		this.rows = rows;
	}

	// TODO: consolidate
	public GridDefinition(String... pov) {
		if (pov.length < 3) throw new IllegalArgumentException("Must provide at least three members");
		int lastElement = pov.length - 1;
		int secondToLastElement = lastElement - 1;
		this.pov = DimensionMembers.of(Arrays.copyOfRange(pov, 0, secondToLastElement));
		this.columns = Arrays.asList(DimensionMembers.of(pov[secondToLastElement]));
		this.rows = Arrays.asList(DimensionMembers.of(pov[lastElement]));
	}

	public GridDefinition(List<String> pov) {
		if (pov.size() < 3) throw new IllegalArgumentException("Must provide at least three members");
		int lastElement = pov.size() - 1;
		int secondToLastElement = lastElement - 1;
		this.pov = DimensionMembers.ofMemberNames(pov.subList(0, secondToLastElement));
		this.columns = Arrays.asList(DimensionMembers.of(pov.get(secondToLastElement)));
		this.rows = Arrays.asList(DimensionMembers.of(pov.get(lastElement)));
	}

	public boolean isSuppressMissingBlocks() {
		return suppressMissingBlocks;
	}

	public void setSuppressMissingBlocks(boolean suppressMissingBlocks) {
		this.suppressMissingBlocks = suppressMissingBlocks;
	}

	public DimensionMembers getPov() {
		return pov;
	}

	public void setPov(DimensionMembers pov) {
		this.pov = pov;
	}

	/**
	 * The collection of items going across the top of the grid
	 *
	 * @return a list of dimension member objects in the order they appear in
	 *         the grid
	 */
	public List<DimensionMembers> getColumns() {
		return columns;
	}

	/**
	 * Columns are modeled... kind of fucking stupidly. Think of it as a single
	 * array for all of the columns, one element per column. The object in that
	 * array is one or more strings that extend down the sheet.
	 *
	 * @param columns the column definition
	 */
	public void setColumns(List<DimensionMembers> columns) {
		this.columns = columns;
	}

	public List<DimensionMembers> getRows() {
		return rows;
	}

	public void setRows(List<DimensionMembers> rows) {
		this.rows = rows;
	}

}