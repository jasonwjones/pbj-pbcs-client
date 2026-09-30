package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.testing.DestructiveIntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Writing a cell named by its aliases rather than its member names.
 *
 * <p>The failure this pins had no symptom. exportdataslice resolves a Default alias itself, so a read
 * named that way works and everything looks fine; importdataslice takes the same alias, answers that
 * it accepted the cell, rejects nothing - and stores nothing. The caller is told it succeeded, the
 * grid refreshes to the value that was already there, and it reads as the write having gone missing.
 *
 * <p>Uses a cell that is empty to begin with, and clears it again at the end.
 */
@Category(DestructiveIntegrationTest.class)
public class PbcsPlanTypeAliasWriteIT extends AbstractVisionCubeIT {

	/** The cell by its member names. Empty in Vision, which is why it is the one being written to. */
	private static final List<String> BY_NAME =
			Arrays.asList("Actual", "FY23", "Final", "USD", "000", "P_000", "Jan", "4110");

	/** The same cell, with the Default aliases an ad hoc grid displays for Entity and Product. */
	private static final List<String> BY_ALIAS =
			Arrays.asList("Actual", "FY23", "Final", "USD", "No Department", "No Product", "Jan", "4110");

	@Test
	public void aCellNamedByItsAliasesIsActuallyWritten() {
		cube.setCell(BY_NAME, PbcsPlanType.IMPORT_MISSING);
		assertThat("the test cell should start empty",
				cube.getCell(BY_NAME), is(PbcsPlanType.EXPORT_MISSING));

		PbcsPlanType.ImportDataResult result = cube.setCell(BY_ALIAS, "4444");

		assertThat(result.getRejectedCells(), is(0));
		assertThat(result.getAcceptedCells(), is(1));
		// The assertion that matters: accepted was always 1, including when nothing was stored.
		assertThat("the write must reach the cube, not merely be accepted",
				cube.getCell(BY_NAME), is("4444"));

		cube.setCell(BY_NAME, PbcsPlanType.IMPORT_MISSING);
	}

	/** And the same cell named by its member names still works, which it always did. */
	@Test
	public void aCellNamedByItsMemberNamesStillWrites() {
		cube.setCell(BY_NAME, "4242");
		assertThat(cube.getCell(BY_NAME), is("4242"));
		cube.setCell(BY_NAME, PbcsPlanType.IMPORT_MISSING);
	}

}
