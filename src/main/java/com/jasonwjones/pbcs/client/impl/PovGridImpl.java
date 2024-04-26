package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.client.Grid;
import com.jasonwjones.pbcs.client.PovGrid;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PovGridImpl<E> implements PovGrid<E> {

    private final List<E> pov;

    private final Grid<E> grid;

    public PovGridImpl(List<E> pov, Grid<E> grid) {
        this.pov = pov;
        this.grid = grid;
    }

    public List<E> foo() {
        return null;
    }

    /**
     * Creates a new grid that's a copy of the given grid
     * @param conversion
     * @return
     * @param <T> the result type
     */
    @Override
    public <T> PovGrid<T> copyOf(Function<E, T> conversion) {
        List<T> pov = getPov().stream().map(conversion).collect(Collectors.toList());
        Grid<T> grid = new HashMapGrid<>(getRows(), getColumns());
        for (int row = 0; row < getRows(); row++) {
            for (int col = 0; col < getColumns(); col++) {
                grid.setCell(row, col, conversion.apply(getCell(row, col)));
            }
        }
        return new PovGridImpl<>(pov, grid);
    }

    @Override
    public int getRows() {
        // SHOULD THIS BE +1 for the POV (and callers that want the data grid size can get that grid and ask for row count)?
        return grid.getRows();
    }

    @Override
    public int getColumns() {
        return grid.getColumns();
    }

    @Override
    public E getCell(int row, int column) {
        return grid.getCell(row, column);
    }

    @Override
    public void setCell(int row, int column, E value) {
        grid.setCell(row, column, value);
    }

    @Override
    public List<E> getPov() {
        return pov;
    }

}