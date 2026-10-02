package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.api.v3.dataslices.DataSlice;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.client.PovGrid;
import com.jasonwjones.pbcs.client.exceptions.PbcsDataImportException;
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
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

/**
 * Reading and writing supporting detail on a live pod.
 *
 * <p>The server stores whatever value it is sent beside the lines and never works one out, so these check
 * the cell's value as well as its lines: the value is the library's arithmetic, stored.
 */
@Category(DestructiveIntegrationTest.class)
public class PbcsPlanTypeSupportingDetailIT extends AbstractVisionCubeIT {

	private static final List<String> POV = Arrays.asList("Actual", "FY23", "Final", "USD", "000", "P_000");

	private static final List<String> CELL = cell("Mar", "4110");

	private static final List<String> NESTED_CELL = cell("Apr", "4110");

	/** 4001 is the stored, level-1 parent of 4110 in Vision. */
	private static final List<String> PARENT_CELL = cell("Mar", "4001");

	/** #Missing deletes a cell's supporting detail along with its value. */
	@After
	public void clearCells() {
		cube.setCellNotes(CELL, Collections.emptyList());
		cube.setCell(CELL, PbcsPlanType.IMPORT_MISSING);
		cube.setCell(NESTED_CELL, PbcsPlanType.IMPORT_MISSING);
	}

	@Test
	public void linesAreStoredWithTheirTotal() {
		List<DataSlice.SupportingDetail> lines = cube.setSupportingDetail(CELL, gasAndWater());

		assertThat(labels(lines), contains("Gas", "Water"));
		assertThat(cube.getCell(CELL), is("400"));
	}

	/** Vision's own 7510, entered through the UI, saved the parent line as 4386 and the cell as 3886. */
	@Test
	public void aParentLineTakesItsChildrensTotal() {
		List<DataSlice.SupportingDetail> lines = cube.setSupportingDetail(NESTED_CELL, Arrays.asList(
				new DataSlice.SupportingDetail("Total Postage", "+", null),
				new DataSlice.SupportingDetail("Employee Count", "+", "43", 1),
				new DataSlice.SupportingDetail("Postage Allocation", "*", "102", 1),
				new DataSlice.SupportingDetail("Rebate", "-", "500")));

		assertThat(lines.get(0).getValue(), is("4386"));
		assertThat(lines.get(1).getGeneration(), is(1));
		assertThat(cube.getCell(NESTED_CELL), is("3886"));
	}

	@Test
	public void anEmptyListDeletesTheDetailAndKeepsTheValue() {
		cube.setSupportingDetail(CELL, gasAndWater());

		assertThat(cube.setSupportingDetail(CELL, Collections.emptyList()), is(empty()));
		assertThat(cube.getCell(CELL), is("400"));
	}

	/** Even the value it already has: what setCell's documentation warns about. */
	@Test
	public void writingAValueDeletesTheDetail() {
		cube.setSupportingDetail(CELL, gasAndWater());

		cube.setCell(CELL, "400");

		assertThat(cube.getSupportingDetail(CELL), is(empty()));
	}

	/** A notes write sends a blank value, which is what keeps it from doing the same. */
	@Test
	public void writingNotesLeavesTheDetailAlone() {
		cube.setSupportingDetail(CELL, gasAndWater());

		cube.setCellNotes(CELL, Collections.singletonList("beside the detail"));

		assertThat(labels(cube.getSupportingDetail(CELL)), contains("Gas", "Water"));
		assertThat(cube.getCell(CELL), is("400"));
	}

	@Test(expected = PbcsDataImportException.class)
	public void anUpperLevelMemberIsRejected() {
		cube.setSupportingDetail(PARENT_CELL, gasAndWater());
	}

	@Test
	public void aRetrieveWithPlanningDataCarriesTheDetail() {
		cube.setSupportingDetail(CELL, gasAndWater());

		HashMapGrid<String> grid = new HashMapGrid<>(2, 2);
		grid.setCell(0, 1, "Mar");
		grid.setCell(1, 0, "4110");
		PovGrid<String> povGrid = new PovGridImpl<>(POV, grid);
		PbcsRetrieveOptionsImpl options = new PbcsRetrieveOptionsImpl();
		options.setExportPlanningData(true);

		DataSliceGrid result = cube.retrieve(povGrid, options);

		DataSliceGrid.DataCell cell = (DataSliceGrid.DataCell) result.getCell(1, 1);
		assertThat(labels(cell.getSupportingDetail()), contains("Gas", "Water"));
	}

	private static List<DataSlice.SupportingDetail> gasAndWater() {
		return Arrays.asList(new DataSlice.SupportingDetail("Gas", "+", "300"), new DataSlice.SupportingDetail("Water", "+", "100"));
	}

	private static List<String> labels(List<DataSlice.SupportingDetail> lines) {
		return lines.stream().map(DataSlice.SupportingDetail::getLabel).toList();
	}

	private static List<String> cell(String period, String account) {
		List<String> cell = new ArrayList<>(POV);
		cell.add(period);
		cell.add(account);
		return Collections.unmodifiableList(cell);
	}

}
