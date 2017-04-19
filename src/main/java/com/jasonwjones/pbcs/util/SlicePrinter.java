package com.jasonwjones.pbcs.util;

import java.util.List;

import com.jasonwjones.pbcs.api.v3.dataslices.DataSlice;
import com.jasonwjones.pbcs.api.v3.dataslices.DataSlice.HeaderDataRow;

/**
 * A simple utilty class for printing a {@link DataSlice} class.
 * 
 * @author jasonwjones
 *
 */
public class SlicePrinter {

	private int defaultWidth = 20;

	private String template = "[%-" + defaultWidth + "s]";

	public void print(DataSlice slice) {

		// find out how many elements are on the 'left' axis
		int leftColumnWidth = getCountOfLeftDimensions(slice);

		printRow(slice.getPov(), leftColumnWidth, true);

		for (List<String> colList : slice.getColumns()) {
			printRow(colList, leftColumnWidth, true);
		}

		for (HeaderDataRow row : slice.getRows()) {
			printRow(row.getHeaders(), 0, false);
			printRow(row.getData(), 0, true);
		}
	}

	public int getCountOfLeftDimensions(DataSlice slice) {
		return slice.getRows().get(0).getHeaders().size();
	}
	
	/**
	 * Prints a data row.
	 * 
	 * @param items the items to be printed
	 * @param spaces the number of empty slots to the left of the items in this
	 *            row (the 'null' OLAP quadrant).
	 * @param newLine whether to print a newline after the cells or not
	 */
	public void printRow(List<String> items, int spaces, boolean newLine) {
		for (int index = 0; index < spaces; index++) {
			System.out.printf(template, "");
		}
		for (String item : items) {
			System.out.printf(template, truncate(item, 20, "`"));
		}
		if (newLine) {
			System.out.println();
		}
	}

	public static String truncate(String text, int maxLength, String truncIndicator) {
		if (text == null) return null;
		if (text.length() > maxLength) {
			return text.subSequence(0, maxLength - truncIndicator.length()) + truncIndicator;
		}
		return text;
	}
	
}
