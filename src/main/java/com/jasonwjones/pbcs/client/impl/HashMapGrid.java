package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.client.Grid;

import java.util.HashMap;
import java.util.Map;

public class HashMapGrid<E> implements Grid<E> {

	private final int rows;

	private final int columns;

	private final Map<Integer, E> data;

	public HashMapGrid(Grid<E> grid) {
		if (grid instanceof HashMapGrid) {
			HashMapGrid<E> hashMapGrid = (HashMapGrid<E>) grid;
			this.rows = hashMapGrid.rows;
			this.columns = hashMapGrid.columns;
			this.data = new HashMap<>(hashMapGrid.data);
		} else {
			this.rows = grid.getRows();
			this.columns = grid.getColumns();
			data = new HashMap<>(rows * columns);
			for (int row = 0; row < grid.getRows(); row++) {
				for (int col = 0; col < grid.getColumns(); col++) {
					setCell(row, col, grid.getCell(row, col));
				}
			}
		}
	}

	public HashMapGrid(int rows, int columns) {
		this.rows = rows;
		this.columns = columns;
		data = new HashMap<>();
	}

	@Override
	public int getRows() {
		return rows;
	}

	@Override
	public int getColumns() {
		return columns;
	}

	@Override
	public E getCell(int row, int column) {
		return data.get(offset(row, column));
	}

	public void setCell(int row, int column, E value) {
		data.put(offset(row, column), value);
	}

	private Integer offset(int row, int column) {
		return row * columns + column;
	}

}