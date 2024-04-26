package com.jasonwjones.pbcs.util;

import com.jasonwjones.pbcs.client.Grid;
import com.jasonwjones.pbcs.client.PovGrid;

import java.io.PrintStream;

public class GridPrinter {

    public static <E> void print(Grid<E> grid) {
        print(grid, System.out);
    }

    public static <E> void print(PovGrid<E> grid) {
        System.out.print("POV: ");
        for (E item : grid.getPov()) {
            System.out.print(item + " ");
        }
        System.out.println();
        print(grid, System.out);
    }

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