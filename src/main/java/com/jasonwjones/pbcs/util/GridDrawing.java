package com.jasonwjones.pbcs.util;

import com.jasonwjones.pbcs.client.Grid;

public class GridDrawing {

    private GridDrawing() {}

    public static <E> void drawRow(Grid<E> grid, int row, int column, Iterable<E> items) {
        for (E item : items) {
            grid.setCell(row, column++, item);
        }
    }

    public static <E> void drawColumn(Grid<E> grid, int row, int column, Iterable<E> items) {
        for (E item : items) {
            grid.setCell(row++, column, item);
        }
    }

}