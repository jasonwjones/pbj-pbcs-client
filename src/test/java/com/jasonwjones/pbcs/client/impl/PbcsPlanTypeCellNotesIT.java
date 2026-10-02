package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.client.PovGrid;
import com.jasonwjones.pbcs.client.impl.grid.DataSliceGrid;
import com.jasonwjones.pbcs.testing.DestructiveIntegrationTest;
import org.junit.After;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

/**
 * Reading and writing cell notes (Planning's cell "comments") on a live pod.
 *
 * <p>Every assertion reads the cell back rather than trusting the import's response, which reports
 * nothing about notes: a notes-only write comes back with zero accepted and zero updated cells whether
 * the notes landed or not.
 */
@Category(DestructiveIntegrationTest.class)
public class PbcsPlanTypeCellNotesIT extends AbstractVisionCubeIT {

	private static final List<String> POV = Arrays.asList("Actual", "FY23", "Final", "USD", "000", "P_000");

	private static final List<String> CELL = cell("Mar", "4110");

	private static final List<String> VALUE_CELL = cell("Mar", "4120");

	/** 4001 is the stored, level-1 parent of 4110 in Vision. */
	private static final List<String> PARENT_CELL = cell("Mar", "4001");

	@After
	public void clearNotes() {
		for (List<String> cell : Arrays.asList(CELL, VALUE_CELL, PARENT_CELL)) {
			cube.setCellNotes(cell, Collections.emptyList());
		}
		cube.setCell(VALUE_CELL, PbcsPlanType.IMPORT_MISSING);
	}

	@Test
	public void overwriteReplacesEveryNote() {
		assertThat(cube.setCellNotes(CELL, Arrays.asList("first", "second")), containsInAnyOrder("first", "second"));

		assertThat(cube.setCellNotes(CELL, Collections.singletonList("third")), contains("third"));
	}

	@Test
	public void appendKeepsTheExistingNotes() {
		cube.setCellNotes(CELL, Collections.singletonList("one"));

		List<String> notes = cube.setCellNotes(CELL, Collections.singletonList("two"), PbcsPlanType.CellNotesOption.APPEND);

		assertThat(notes, containsInAnyOrder("one", "two"));
	}

	@Test
	public void anEmptyListDeletesTheNotes() {
		cube.setCellNotes(CELL, Collections.singletonList("doomed"));

		assertThat(cube.setCellNotes(CELL, Collections.emptyList()), is(empty()));
		assertThat(cube.getCellNotes(CELL), is(empty()));
	}

	/** No HTML conversion either way: what goes in comes back. */
	@Test
	public void contentsRoundTripExactly() {
		String contents = "line one\nline two <tag> & \"quoted\" é";

		assertThat(cube.setCellNotes(CELL, Collections.singletonList(contents)), contains(contents));
	}

	@Test
	public void writingNotesLeavesTheValueAlone() {
		cube.setCell(VALUE_CELL, "55");

		cube.setCellNotes(VALUE_CELL, Collections.singletonList("beside a value"));

		assertThat(cube.getCell(VALUE_CELL), is("55"));
		assertThat(cube.getCellNotes(VALUE_CELL), contains("beside a value"));
	}

	/**
	 * The server takes a note on an upper-level member without complaint and does not store it. Pinned
	 * here so that a pod which starts storing them is noticed.
	 */
	@Test
	public void anUpperLevelMemberTakesNoNotes() {
		assertThat(cube.setCellNotes(PARENT_CELL, Collections.singletonList("on a parent")), is(empty()));
	}

	@Test
	public void aRetrieveWithPlanningDataCarriesTheNotes() {
		cube.setCellNotes(CELL, Collections.singletonList("seen by retrieve"));

		HashMapGrid<String> grid = new HashMapGrid<>(2, 2);
		grid.setCell(0, 1, "Mar");
		grid.setCell(1, 0, "4110");
		PovGrid<String> povGrid = new PovGridImpl<>(POV, grid);
		PbcsRetrieveOptionsImpl options = new PbcsRetrieveOptionsImpl();
		options.setExportPlanningData(true);

		DataSliceGrid result = cube.retrieve(povGrid, options);

		DataSliceGrid.DataCell cell = (DataSliceGrid.DataCell) result.getCell(1, 1);
		assertThat(cell.getCellNotes(), contains("seen by retrieve"));
	}

	private static List<String> cell(String period, String account) {
		List<String> cell = new ArrayList<>(POV);
		cell.add(period);
		cell.add(account);
		return Collections.unmodifiableList(cell);
	}

}
