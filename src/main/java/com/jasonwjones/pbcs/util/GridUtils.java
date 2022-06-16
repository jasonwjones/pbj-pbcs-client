package com.jasonwjones.pbcs.util;

import com.jasonwjones.pbcs.client.Grid;
import com.jasonwjones.pbcs.client.impl.HashMapGrid;

import java.util.*;
import java.util.function.BiFunction;

public class GridUtils {

    private GridUtils() {}

    public static <E> List<E> row(Grid<E> grid, int rowIndex) {
        return row(grid, rowIndex, 0);
    }

    public static <E> List<E> row(Grid<E> grid, int rowIndex, int startCol) {
        List<E> row = new ArrayList<>(grid.getColumns() - startCol);
        for (int col = startCol; col < grid.getColumns(); col++) {
            row.add(grid.getCell(rowIndex, col));
        }
        return row;
    }

    public static <E> Set<E> nonNullRowItems(Grid<E> grid, int rowIndex) {
        Set<E> items = new HashSet<>();
        for (int col = 0; col < grid.getColumns(); col++) {
            E item = grid.getCell(rowIndex, col);
            if (item != null) items.add(item);
        }
        return items;
    }

    public static <E> Set<E> nonNullColItems(Grid<E> grid, int colIndex) {
        Set<E> items = new HashSet<>();
        for (int row = 0; row < grid.getRows(); row++) {
            E item = grid.getCell(row, colIndex);
            if (item != null) items.add(item);
        }
        return items;
    }

    public static <E> List<E> col(Grid<E> grid, int colIndex) {
        return col(grid, colIndex, 0);
    }

    public static <E> List<E> col(Grid<E> grid, int colIndex, int startRow) {
        List<E> col = new ArrayList<>(grid.getRows() - startRow);
        for (int row = startRow; row < grid.getRows(); row++) {
            col.add(grid.getCell(row, colIndex));
        }
        return col;
    }

    public static <E> List<E> col(Grid<E> grid, int colIndex, int startRow, int endRow) {
        List<E> col = new ArrayList<>();
        for (int row = startRow; row < endRow; row++) {
            col.add(grid.getCell(row, colIndex));
        }
        return col;
    }

    public static <E> List<E> row(Grid<E> grid, int rowIndex, int startCol, int endCol) {
        List<E> row = new ArrayList<>();
        for (int col = startCol; col < endCol; col++) {
            row.add(grid.getCell(rowIndex, col));
        }
        return row;
    }

    public static Grid<String> stringGrid(int rows, int columns) {
        return grid(rows, columns, "%d,%d");
    }

    public static Grid<String> grid(int rows, int columns, String format) {
         return grid(rows, columns, (row, col) -> String.format(format, row, col));
    }

    public static <E> Grid<E> grid(int rows, int columns, BiFunction<Integer, Integer, E> creator) {
        Grid<E> grid = new HashMapGrid<>(rows, columns);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                grid.setCell(row, col, creator.apply(row, col));
            }
        }
        return grid;
    }

    public static int firstNonNullInColumn(Grid<?> grid, int column) {
        for (int row = 0; row < grid.getRows(); row++) {
            if (grid.getCell(row, column) != null) return row;
        }
        return -1;
    }

    public static int firstNonNullInRow(Grid<?> grid, int row) {
        for (int col = 0; col < grid.getColumns(); col++) {
            if (grid.getCell(row, col) != null) return col;
        }
        return -1;
    }

    public static int lastNonNullInRow(Grid<?> grid, int row) {
        for (int col = grid.getColumns() - 1; col >= 0; col--) {
            if (grid.getCell(row, col) != null) return col;
        }
        return -1;
    }

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