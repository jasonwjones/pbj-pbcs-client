package com.jasonwjones.pbcs.util;

import com.jasonwjones.pbcs.client.PovGrid;
import com.jasonwjones.pbcs.client.impl.grid.DataSliceGrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PovGridWalker {

    private PovGridWalker() {}

    public static <E> Map<List<E>, E> walk(PovGrid<E> grid) {
        final int firstRow = GridUtils.firstInColumn(grid, 0, v -> v != null && v != DataSliceGrid.BLANK);
        final int firstCol = GridUtils.firstInRow(grid, 0, v -> v != null && v != DataSliceGrid.BLANK);

        final Map<List<E>, E> cells = new HashMap<>();

        for (int row = firstRow; row < grid.getRows(); row++) {
            for (int col = firstCol; col < grid.getColumns(); col++) {
                List<E> headers = new ArrayList<>(grid.getPov());
                for (int headerRow = 0; headerRow < firstRow; headerRow++) {
                    headers.add(grid.getCell(headerRow, col));
                }
                for (int headerColumn = 0; headerColumn < firstCol; headerColumn++) {
                    headers.add(grid.getCell(row, headerColumn));
                }
                E cellValue = grid.getCell(row, col);
                cells.put(headers, cellValue);
            }
        }
        return cells;
    }

}