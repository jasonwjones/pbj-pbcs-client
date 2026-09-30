package com.jasonwjones.pbcs.client.exceptions;

import com.jasonwjones.pbcs.api.v3.dataslices.ImportDataSliceResponse;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * What the failure says, as opposed to how much of it there was.
 *
 * <p>The import always asks for the rejected cells and their reasons, so the server has already said
 * which intersection it refused and why. A message with only a count leaves whoever reads it with
 * nothing to act on.
 */
public class PbcsDataImportExceptionTest {

	private static ImportDataSliceResponse.RejectedCellDetails cell(java.util.List<String> members,
			java.util.List<String> readOnly, java.util.List<String> other) {
		ImportDataSliceResponse.RejectedCellDetails details = new ImportDataSliceResponse.RejectedCellDetails();
		details.setMemberNames(members);
		details.setReadOnlyReasons(readOnly);
		details.setOtherReasons(other);
		return details;
	}

	@Test
	public void theReasonIsInTheMessage() {
		ImportDataSliceResponse response = new ImportDataSliceResponse();
		response.setNumAcceptedCells(0);
		response.setNumRejectedCells(1);
		response.setRejectedCellsWithDetails(Collections.singletonList(cell(
				Arrays.asList("FY22", "Jan", "4001", "Final", "Actual"),
				Collections.singletonList("The member is a calculated member"),
				Collections.<String>emptyList())));

		String message = new PbcsDataImportException(response).getMessage();

		assertThat(message, containsString("Failed to import 1 cells"));
		assertThat(message, containsString("FY22 -> Jan -> 4001 -> Final -> Actual"));
		assertThat(message, containsString("The member is a calculated member"));
	}

	/** Several rejected cells are all named, not just the first. */
	@Test
	public void everyRejectedCellIsNamed() {
		ImportDataSliceResponse response = new ImportDataSliceResponse();
		response.setNumRejectedCells(2);
		response.setRejectedCellsWithDetails(Arrays.asList(
				cell(Collections.singletonList("Jan"), Collections.singletonList("Read only"), null),
				cell(Collections.singletonList("Feb"), null, Collections.singletonList("No such intersection"))));

		String message = new PbcsDataImportException(response).getMessage();

		assertThat(message, containsString("Jan"));
		assertThat(message, containsString("Read only"));
		assertThat(message, containsString("Feb"));
		assertThat(message, containsString("No such intersection"));
	}

	/**
	 * Without the details, the coordinates alone are still worth saying.
	 *
	 * <p>A server that does not honour IncludeRejectedCellsWithDetails may still honour
	 * IncludeRejectedCells, and knowing which cell failed is most of the answer.
	 */
	@Test
	public void theCoordinatesAreUsedWhenThereAreNoDetails() {
		ImportDataSliceResponse response = new ImportDataSliceResponse();
		response.setNumRejectedCells(1);
		response.setRejectedCells(Collections.singletonList("FY22, Jan, 4001"));

		String message = new PbcsDataImportException(response).getMessage();

		assertThat(message, containsString("FY22, Jan, 4001"));
	}

	/** And a response that says nothing beyond the counts still reads as it always did. */
	@Test
	public void theCountAloneIsStillReported() {
		ImportDataSliceResponse response = new ImportDataSliceResponse();
		response.setNumAcceptedCells(3);
		response.setNumRejectedCells(2);

		PbcsDataImportException exception = new PbcsDataImportException(response);

		assertThat(exception.getMessage(), is("Failed to import 2 cells (successful: 3)"));
		assertThat(exception.getMessage(), not(containsString("\n")));
		assertThat(exception.getNumRejectedCells(), is(2));
		assertThat(exception.getNumAcceptedCells(), is(3));
	}

}
