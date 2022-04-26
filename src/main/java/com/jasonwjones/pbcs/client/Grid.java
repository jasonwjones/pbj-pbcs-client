package com.jasonwjones.pbcs.client;

public interface Grid<E> {

    int getRows();

    int getColumns();

    E getCell(int row, int column);

    void setCell(int row, int column, E value);

}