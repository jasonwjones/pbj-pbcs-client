package com.jasonwjones.pbcs.api.v3.dataslices;

import java.util.List;

/**
 * Small helper for printing lists of lists (e.g., grid rows) to the console for debugging purposes.
 */
public class ArrayUtils {

	/**
	 * Constructs an instance of this utility class.
	 */
	public ArrayUtils() {
	}

	/**
	 * Prints each row of the given list of lists to {@link System#out}, with each cell padded to a fixed width.
	 *
	 * @param data the rows to print
	 */
	public static void printLists(List<List<String>> data) {

		for (List<String> list : data) {
			for (String cell : list) {
				System.out.printf("[%20s]", cell);
			}
			System.out.println();
		}

	}

}
