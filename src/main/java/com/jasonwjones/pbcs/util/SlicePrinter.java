package com.jasonwjones.pbcs.util;

import java.util.List;

import com.jasonwjones.pbcs.api.v3.dataslices.DataSlice;
import com.jasonwjones.pbcs.api.v3.dataslices.DataSlice.HeaderDataRow;

/**
 * A simple utility class for printing a {@link DataSlice} class.
 *
 * @author jasonwjones
 *
 */
public class SlicePrinter {

	private int defaultWidth = 20;

	private String template = "[%-" + defaultWidth + "s]";

	/**
	 * Constructs an instance of this printer.
	 */
	public SlicePrinter() {
	}

	/**
	 * Prints the given data slice to {@link System#out}.
	 *
	 * @param slice the data slice to print
	 */
	public void print(DataSlice slice) {

		// find out how many elements are on the 'left' axis
		int leftColumnWidth = getCountOfLeftDimensions(slice);

		printRow(slice.getPov(), 1, true);

		for (List<String> colList : slice.getColumns()) {
			printRow(colList, leftColumnWidth, true);
		}

		for (HeaderDataRow row : slice.getRows()) {
			printRow(row.getHeaders(), 0, false);
			printRow(row.getData(), 0, true);
		}
	}

	/**
	 * Prints the given data slice by calling the given delegate for each cell, instead of printing directly
	 * to a stream.
	 *
	 * @param dataSlice the data slice to print
	 * @param delegate the delegate to call for each cell
	 */
	public void print(DataSlice dataSlice, Delegate delegate) {
		int column = 1;

		for (String povElement : dataSlice.getPov()) {
			delegate.print(0, column++, povElement, false);
		}

		int row = 1;
		int leftColumnWidth = getCountOfLeftDimensions(dataSlice);

		for (List<String> colList : dataSlice.getColumns()) {
			column = leftColumnWidth;
			for (String topElement : colList) {
				delegate.print(row, column++, topElement, false);
			}
			row++;
		}

		for (HeaderDataRow headerDataRow : dataSlice.getRows()) {
			column = 0;
			for (String leftElement : headerDataRow.getHeaders()) {
				delegate.print(row, column++, leftElement, false);
			}
			for (String cell : headerDataRow.getData()) {
				delegate.print(row,  column++, cell, true);
			}
			row++;
		}
	}

	/**
	 * Gets the number of member headers on the left axis of the given data slice.
	 *
	 * @param slice the data slice
	 * @return the count of left-axis dimensions
	 */
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

	/**
	 * Truncates the given text to the given max length, appending the given indicator if truncated.
	 *
	 * @param text the text to truncate, may be null
	 * @param maxLength the max length
	 * @param truncIndicator the indicator to append when truncated
	 * @return the truncated text, or null if text was null
	 */
	public static String truncate(String text, int maxLength, String truncIndicator) {
		if (text == null) return null;
		if (text.length() > maxLength) {
			return text.subSequence(0, maxLength - truncIndicator.length()) + truncIndicator;
		}
		return text;
	}

	/**
	 * Callback used by {@link #print(DataSlice, Delegate)} to receive each printed cell.
	 */
	public static interface Delegate {

		/**
		 * Called for each cell to print.
		 *
		 * @param row the row of the cell
		 * @param column the column of the cell
		 * @param value the cell value
		 * @param data true if this is a data cell, false if it is a header cell
		 */
		public void print(int row, int column, String value, boolean data);

	}

}
