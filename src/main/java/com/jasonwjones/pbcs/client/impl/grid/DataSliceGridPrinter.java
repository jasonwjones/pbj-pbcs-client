package com.jasonwjones.pbcs.client.impl.grid;

import java.sql.SQLOutput;

public class DataSliceGridPrinter {

    public static void print(DataSliceGrid grid) {
        System.out.print("POV: ");
        for (DataSliceGrid.Cell cell : grid.getPov()) {
            System.out.print(cell.getValue() + " ");
        }
        System.out.println();

        for (int row = 0; row < grid.getRows(); row++) {
            for (int col = 0; col < grid.getColumns(); col++) {
                DataSliceGrid.Cell cell = grid.getCell(row, col);
                String contents = cell.getValue() == null ? "" : cell.getValue();
                System.out.printf("%30s", contents);
            }
            System.out.println();
        }
    }

}