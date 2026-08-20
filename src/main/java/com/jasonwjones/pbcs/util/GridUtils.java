package com.jasonwjones.pbcs.util;

import com.jasonwjones.pbcs.client.Grid;
import com.jasonwjones.pbcs.client.impl.HashMapGrid;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * Static helpers for reading rows/columns from, and building, {@link Grid} instances.
 */
public class GridUtils {

    private GridUtils() {}

    /**
     * Gets an entire row of the grid.
     *
     * @param grid the grid
     * @param rowIndex the row
     * @return the row, as a list
     * @param <E> the type of grid
     */
    public static <E> List<E> row(Grid<E> grid, int rowIndex) {
        return row(grid, rowIndex, 0);
    }

    /**
     * Creates a new grid containing the rows of the given grid starting at the given row.
     *
     * @param grid the source grid
     * @param startRow the first row to include
     * @return a new grid containing the given grid's rows from startRow onward
     * @param <E> the type of grid
     */
    public static <E> Grid<E> subgrid(Grid<E> grid, int startRow) {
        Grid<E> subGrid = new HashMapGrid<>(grid.getRows() - startRow, grid.getColumns());
        for (int row = startRow; row < grid.getRows(); row++) {
            for (int col = 0; col < grid.getColumns(); col++) {
                E value = grid.getCell(row, col);
                subGrid.setCell(row - startRow, col, value);
            }
        }
        return subGrid;
    }

    /**
     * Gets a row of the grid starting at the given column.
     *
     * @param grid the grid
     * @param rowIndex the row
     * @param startCol the starting column, inclusive
     * @return the slice of the row, as a list
     * @param <E> the type of grid
     */
    public static <E> List<E> row(Grid<E> grid, int rowIndex, int startCol) {
        List<E> row = new ArrayList<>(grid.getColumns() - startCol);
        for (int col = startCol; col < grid.getColumns(); col++) {
            row.add(grid.getCell(rowIndex, col));
        }
        return row;
    }

    /**
     * Gets the non-null items in the given row.
     *
     * @param grid the grid
     * @param rowIndex the row
     * @return the non-null items in that row
     * @param <E> the type of grid
     */
    public static <E> Set<E> nonNullRowItems(Grid<E> grid, int rowIndex) {
        Set<E> items = new HashSet<>();
        for (int col = 0; col < grid.getColumns(); col++) {
            E item = grid.getCell(rowIndex, col);
            if (item != null) items.add(item);
        }
        return items;
    }

    /**
     * Gets the non-null items in the given column.
     *
     * @param grid the grid
     * @param colIndex the column
     * @return the non-null items in that column
     * @param <E> the type of grid
     */
    public static <E> Set<E> nonNullColItems(Grid<E> grid, int colIndex) {
        Set<E> items = new HashSet<>();
        for (int row = 0; row < grid.getRows(); row++) {
            E item = grid.getCell(row, colIndex);
            if (item != null) items.add(item);
        }
        return items;
    }

    /**
     * Gets an entire column of the grid.
     *
     * @param grid the grid
     * @param colIndex the column
     * @return the column, as a list
     * @param <E> the type of grid
     */
    public static <E> List<E> col(Grid<E> grid, int colIndex) {
        return col(grid, colIndex, 0);
    }

    /**
     * Gets a column of the grid starting at the given row.
     *
     * @param grid the grid
     * @param colIndex the column
     * @param startRow the starting row, inclusive
     * @return the slice of the column, as a list
     * @param <E> the type of grid
     */
    public static <E> List<E> col(Grid<E> grid, int colIndex, int startRow) {
        List<E> col = new ArrayList<>(grid.getRows() - startRow);
        for (int row = startRow; row < grid.getRows(); row++) {
            col.add(grid.getCell(row, colIndex));
        }
        return col;
    }

    /**
     * Gets a column of the grid between the given row indices.
     *
     * @param grid the grid
     * @param colIndex the column
     * @param startRow the starting row, inclusive
     * @param endRow the ending row, exclusive
     * @return the slice of the column, as a list
     * @param <E> the type of grid
     */
    public static <E> List<E> col(Grid<E> grid, int colIndex, int startRow, int endRow) {
        List<E> col = new ArrayList<>();
        for (int row = startRow; row < endRow; row++) {
            col.add(grid.getCell(row, colIndex));
        }
        return col;
    }

    /**
     * Returns a single row of the grid from between the given column indices
     *
     * @param grid the grid
     * @param rowIndex the row
     * @param startCol the starting column, inclusive
     * @param endCol the ending column, exclusive
     * @return the slice of the grid, as a list
     * @param <E> the type of grid
     */
    public static <E> List<E> row(Grid<E> grid, int rowIndex, int startCol, int endCol) {
        List<E> row = new ArrayList<>();
        for (int col = startCol; col < endCol; col++) {
            row.add(grid.getCell(rowIndex, col));
        }
        return row;
    }

    /**
     * Creates a grid of the given size where each cell contains its "row,col" coordinates as a string.
     *
     * @param rows the number of rows
     * @param columns the number of columns
     * @return the new grid
     */
    public static Grid<String> stringGrid(int rows, int columns) {
        return grid(rows, columns, "%d,%d");
    }

    /**
     * Creates a grid of the given size where each cell is formatted from its row and column using the given
     * format string.
     *
     * @param rows the number of rows
     * @param columns the number of columns
     * @param format a {@link String#format(String, Object...)} format string taking the row and column as arguments
     * @return the new grid
     */
    public static Grid<String> grid(int rows, int columns, String format) {
         return grid(rows, columns, (row, col) -> String.format(format, row, col));
    }

    /**
     * Creates a grid of the given size where each cell is produced by the given creator function from its
     * row and column.
     *
     * @param rows the number of rows
     * @param columns the number of columns
     * @param creator a function producing a cell value from its row and column
     * @return the new grid
     * @param <E> the cell type
     */
    public static <E> Grid<E> grid(int rows, int columns, BiFunction<Integer, Integer, E> creator) {
        Grid<E> grid = new HashMapGrid<>(rows, columns);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                grid.setCell(row, col, creator.apply(row, col));
            }
        }
        return grid;
    }

    /**
     * Finds the row of the first non-null cell in the given column.
     *
     * @param grid the grid
     * @param column the column to search
     * @return the row index of the first non-null cell, or -1 if none found
     */
    public static int firstNonNullInColumn(Grid<?> grid, int column) {
        for (int row = 0; row < grid.getRows(); row++) {
            if (grid.getCell(row, column) != null) return row;
        }
        return -1;
    }

    /**
     * Finds the row of the first cell in the given column matching the given predicate.
     *
     * @param grid the grid
     * @param column the column to search
     * @param predicate the predicate to match
     * @return the row index of the first matching cell, or -1 if none found
     * @param <E> the cell type
     */
    public static <E> int firstInColumn(Grid<E> grid, int column, Predicate<E> predicate) {
        for (int row = 0; row < grid.getRows(); row++) {
            if (predicate.test(grid.getCell(row, column))) return row;
        }
        return -1;
    }

    /**
     * Finds the column of the first cell in the given row matching the given predicate.
     *
     * @param grid the grid
     * @param row the row to search
     * @param predicate the predicate to match
     * @return the column index of the first matching cell, or -1 if none found
     * @param <E> the cell type
     */
    public static <E> int firstInRow(Grid<E> grid, int row, Predicate<E> predicate) {
        for (int col = 0; col < grid.getColumns(); col++) {
            if (predicate.test(grid.getCell(row, col))) return col;
        }
        return -1;
    }

    /**
     * Finds the column of the first non-null cell in the given row.
     *
     * @param grid the grid
     * @param row the row to search
     * @return the column index of the first non-null cell, or -1 if none found
     */
    public static int firstNonNullInRow(Grid<?> grid, int row) {
        for (int col = 0; col < grid.getColumns(); col++) {
            if (grid.getCell(row, col) != null) return col;
        }
        return -1;
    }

    /**
     * Finds the column of the last non-null cell in the given row.
     *
     * @param grid the grid
     * @param row the row to search
     * @return the column index of the last non-null cell, or -1 if none found
     */
    public static int lastNonNullInRow(Grid<?> grid, int row) {
        for (int col = grid.getColumns() - 1; col >= 0; col--) {
            if (grid.getCell(row, col) != null) return col;
        }
        return -1;
    }

    /**
     * Prints the given grid to {@link System#out} for debugging purposes.
     *
     * @param grid the grid to print
     * @param <E> the cell type
     */
    public static <E> void print(Grid<E> grid) {
        for (int row = 0; row < grid.getRows(); row++) {
            for (int col = 0; col < grid.getColumns(); col++) {
                E value = grid.getCell(row, col);
                String printable = value != null ? value.toString() : "";
                System.out.printf("[%20s]", printable);
            }
            System.out.println();
        }

    }

}