package com.jasonwjones.pbcs.util;

import com.jasonwjones.pbcs.client.Grid;
import com.jasonwjones.pbcs.client.PovGrid;

import java.io.PrintStream;

/**
 * Prints a {@link Grid} or {@link PovGrid} to a stream for debugging purposes.
 */
public class GridPrinter {

    /**
     * Constructs an instance of this utility class.
     */
    public GridPrinter() {
    }

    /**
     * Prints the given grid to {@link System#out}.
     *
     * @param grid the grid to print
     * @param <E> the cell type
     */
    public static <E> void print(Grid<E> grid) {
        print(grid, System.out);
    }

    /**
     * Prints the given grid's POV, followed by the grid itself, to {@link System#out}.
     *
     * @param grid the grid to print
     * @param <E> the cell type
     */
    public static <E> void print(PovGrid<E> grid) {
        System.out.print("POV: ");
        for (E item : grid.getPov()) {
            System.out.print(item + " ");
        }
        System.out.println();
        print(grid, System.out);
    }

    /**
     * Prints the given grid to the given stream.
     *
     * @param grid the grid to print
     * @param printStream the stream to print to
     * @param <E> the cell type
     */
    public static <E> void print(Grid<E> grid, PrintStream printStream) {
        for (int row = 0; row < grid.getRows(); row++) {
            for (int col = 0; col < grid.getColumns(); col++) {
                E value = grid.getCell(row, col);
                String display = value != null ? value.toString() : "";
                printStream.print(String.format("[%20s]", display));
            }
            printStream.println();
        }
    }

}
