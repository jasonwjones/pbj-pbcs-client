package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.testing.DestructiveIntegrationTest;
import com.jasonwjones.pbcs.util.DataSliceDiff;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Asking a single-cell write what it actually changed.
 *
 * <p>The option existed and setCell read straight past it, so a caller who asked was handed an empty
 * map and no hint that nothing had looked. It matters more here than it sounds: the counts EPM
 * returns cannot answer the question - a cell is reported accepted that was never stored - so
 * reading the cell back is the only answer that is about the cube rather than about the request.
 */
@Category(DestructiveIntegrationTest.class)
public class PbcsPlanTypeChangedCellsIT extends AbstractVisionCubeIT {

	private static final List<String> CELL =
			Arrays.asList("Actual", "FY23", "Final", "USD", "000", "P_000", "Feb", "4110");

	private static PbcsPlanTypeImpl.ImportDataOptionsImpl reportingChanges() {
		PbcsPlanTypeImpl.ImportDataOptionsImpl options = new PbcsPlanTypeImpl.ImportDataOptionsImpl();
		options.setReturnChangedCells(true);
		return options;
	}

	@Test
	public void aChangedCellIsReportedWithItsBeforeAndAfter() {
		cube.setCell(CELL, "11");

		PbcsPlanType.ImportDataResult result = cube.setCell(CELL, "22", reportingChanges());

		Map<Set<String>, DataSliceDiff.ValChange> changes = result.getChanges();
		assertThat(changes.size(), is(1));
		DataSliceDiff.ValChange change = changes.get(new HashSet<>(CELL));
		assertThat("keyed by the cell's own members", change != null, is(true));
		assertThat(change.getBefore(), is("11"));
		assertThat(change.getAfter(), is("22"));

		cube.setCell(CELL, PbcsPlanType.IMPORT_MISSING);
	}

	/** Writing the value a cell already holds changes nothing, and says so. */
	@Test
	public void writingTheSameValueReportsNoChange() {
		cube.setCell(CELL, "33");

		PbcsPlanType.ImportDataResult result = cube.setCell(CELL, "33", reportingChanges());

		assertThat(result.getChanges().isEmpty(), is(true));

		cube.setCell(CELL, PbcsPlanType.IMPORT_MISSING);
	}

	/** And a write that does not ask pays for nothing and still answers the contract. */
	@Test
	public void notAskingCostsNothingAndStillReturnsAMap() {
		PbcsPlanType.ImportDataResult result = cube.setCell(CELL, "44");

		assertThat(result.getChanges().isEmpty(), is(true));

		cube.setCell(CELL, PbcsPlanType.IMPORT_MISSING);
	}

}
