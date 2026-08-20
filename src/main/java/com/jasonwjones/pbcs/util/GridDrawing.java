package com.jasonwjones.pbcs.util;

import com.jasonwjones.pbcs.client.Grid;

/**
 * Helpers for writing a sequence of values into a {@link Grid} along a row or column.
 */
public class GridDrawing {

    private GridDrawing() {}

    /**
     * Writes the given items into the grid starting at (row, column) and proceeding across the row.
     *
     * @param grid the grid to write into
     * @param row the starting row
     * @param column the starting column
     * @param items the items to write
     * @param <E> the cell type
     */
    public static <E> void drawRow(Grid<E> grid, int row, int column, Iterable<E> items) {
        for (E item : items) {
            grid.setCell(row, column++, item);
        }
    }

    /**
     * Writes the given items into the grid starting at (row, column) and proceeding down the column.
     *
     * @param grid the grid to write into
     * @param row the starting row
     * @param column the starting column
     * @param items the items to write
     * @param <E> the cell type
     */
    public static <E> void drawColumn(Grid<E> grid, int row, int column, Iterable<E> items) {
        for (E item : items) {
            grid.setCell(row++, column, item);
        }
    }

}
