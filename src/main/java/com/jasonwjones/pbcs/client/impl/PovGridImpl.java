package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.client.Grid;
import com.jasonwjones.pbcs.client.PovGrid;

import java.util.List;

public class PovGridImpl<E> implements PovGrid<E> {

    private final List<E> pov;

    private final Grid<E> grid;

    public PovGridImpl(List<E> pov, Grid<E> grid) {
        this.pov = pov;
        this.grid = grid;
    }

    @Override
    public int getRows() {
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