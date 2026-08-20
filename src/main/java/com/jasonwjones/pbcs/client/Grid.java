package com.jasonwjones.pbcs.client;

/**
 * A simple two-dimensional grid of cells, addressed by zero-based row and column index.
 *
 * @param <E> the cell type
 */
public interface Grid<E> {

    /**
     * Gets the number of rows in this grid.
     *
     * @return the row count
     */
    int getRows();

    /**
     * Gets the number of columns in this grid.
     *
     * @return the column count
     */
    int getColumns();

    /**
     * Gets the cell at the given row and column.
     *
     * @param row the row index
     * @param column the column index
     * @return the cell at that position
     */
    E getCell(int row, int column);

    /**
     * Sets the cell at the given row and column.
     *
     * @param row the row index
     * @param column the column index
     * @param value the value to set
     */
    void setCell(int row, int column, E value);

}
