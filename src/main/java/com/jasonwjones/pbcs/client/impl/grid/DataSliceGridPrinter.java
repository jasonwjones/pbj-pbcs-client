package com.jasonwjones.pbcs.client.impl.grid;

/**
 * Prints a {@link DataSliceGrid} to the console for debugging purposes.
 */
public class DataSliceGridPrinter {

    /**
     * Constructs an instance of this utility class.
     */
    public DataSliceGridPrinter() {
    }

    /**
     * Prints the given grid to {@link System#out}.
     *
     * @param grid the grid to print
     */
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
