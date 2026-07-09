package com.jasonwjones.pbcs.api.v3.dataslices;

import com.jasonwjones.pbcs.client.PovGrid;
import com.jasonwjones.pbcs.util.GridUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

/**
 * Main grid definition that is inside an {@link ExportDataSlice} object.
 */
public class GridDefinition {

	private boolean suppressMissingBlocks = false;

	private boolean suppressMissingRows = false;

	private boolean suppressMissingColumns = false;

	private DimensionMembers pov;

	private List<DimensionMembers> columns;

	private List<DimensionMembers> rows;

	public GridDefinition() {}

	public GridDefinition(List<String> pov, List<DimensionMembers> columns, List<DimensionMembers> rows) {
		this.pov = DimensionMembers.ofMemberNames(pov);
		this.columns = columns;
		this.rows = rows;
	}

	public GridDefinition(PovGrid<String> grid) {
		this.pov = DimensionMembers.ofMemberNames(grid.getPov());
		// get the 'fulcrum' point in the grid
		int firstRowWithCell = GridUtils.firstNonNullInColumn(grid, 0);
		int firstColWithCell = GridUtils.firstNonNullInRow(grid, 0);
		int lastNonNullCol = GridUtils.lastNonNullInRow(grid, 0);

		List<DimensionMembers> top = new ArrayList<>();
		for (int col = firstColWithCell; col <= lastNonNullCol; col++) {
			List<String> members = GridUtils.col(grid, col, 0, firstRowWithCell);
			DimensionMembers dimensionMembers = DimensionMembers.ofMemberNames(members);
			top.add(dimensionMembers);
		}

		List<DimensionMembers> left = new ArrayList<>();
		List<List<String>> columns = new ArrayList<>();
		for (int col = 0; col < firstColWithCell; col++) {
			List<String> colMembers = GridUtils.col(grid, col, firstRowWithCell, grid.getRows());
			columns.add(colMembers);
		}
		DimensionMembers leftDimMembers = DimensionMembers.of(columns);
		left.add(leftDimMembers);

		this.columns = top;
		this.rows = left;
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

	public boolean isSuppressMissingColumns() {
		return suppressMissingColumns;
	}

	public void setSuppressMissingColumns(boolean suppressMissingColumns) {
		this.suppressMissingColumns = suppressMissingColumns;
	}

	public boolean isSuppressMissingRows() {
		return suppressMissingRows;
	}

	public void setSuppressMissingRows(boolean suppressMissingRows) {
		this.suppressMissingRows = suppressMissingRows;
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
	 * array for all the columns, one element per column. The object in that
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

	@Override
	public String toString() {
		return new StringJoiner(", ", GridDefinition.class.getSimpleName() + "[", "]")
				.add("rows=" + rows.size())
				.add("suppressMissingBlocks=" + suppressMissingBlocks)
				.add("suppressMissingColumns=" + suppressMissingColumns)
				.add("suppressMissingRows=" + suppressMissingRows)
				.toString();
	}

}