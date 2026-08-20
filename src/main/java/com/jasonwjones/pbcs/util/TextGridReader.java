package com.jasonwjones.pbcs.util;

import com.jasonwjones.pbcs.client.Grid;
import com.jasonwjones.pbcs.client.PovGrid;
import com.jasonwjones.pbcs.client.impl.HashMapGrid;
import com.jasonwjones.pbcs.client.impl.PovGridImpl;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Reads a delimited text file or string into a {@link Grid}, primarily intended for building test fixtures.
 */
public class TextGridReader {

    /**
     * Constructs an instance of this reader.
     */
    public TextGridReader() {
    }

    /**
     * Reads a comma-delimited classpath resource into a grid.
     *
     * @param resourceName the classpath resource name
     * @return the resulting grid
     * @throws IOException if the resource cannot be read
     */
    public Grid<String> read(String resourceName) throws IOException {
        Resource resource = new ClassPathResource(resourceName);
        return read(resource.getInputStream(), ",");
    }

    /**
     * Reads a delimited stream into a grid.
     *
     * @param inputStream the stream to read
     * @param separator the cell delimiter
     * @return the resulting grid
     * @throws IOException if the stream cannot be read
     */
    public Grid<String> read(InputStream inputStream, String separator) throws IOException {
        String gridText = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
        return read(gridText, separator);
    }

    /**
     * Reads a comma-delimited classpath resource into a POV grid, treating the first line as the POV.
     *
     * @param resourceName the classpath resource name
     * @return the resulting POV grid
     * @throws IOException if the resource cannot be read
     */
    public PovGrid<String> readPovGridFromFile(String resourceName) throws IOException {
        Resource resource = new ClassPathResource(resourceName);
        String gridText = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8.name());
        return readPovGrid(gridText, ",");
    }

    /**
     * Reads delimited text into a POV grid, treating the first line as the POV.
     *
     * @param gridText the delimited text
     * @param separator the cell delimiter
     * @return the resulting POV grid
     */
    public PovGrid<String> readPovGrid(String gridText, String separator) {
        String lines[] = gridText.split("\\r?\\n");
        List<String> pov = new ArrayList<>();
        String[] povCells = split(lines[0], separator);
        for (String povCell : povCells) {
            if (povCell != null && !povCell.trim().isEmpty()) {
                pov.add(povCell);
            }
        }
        Grid<String> grid = read(Arrays.copyOfRange(lines, 1, lines.length), separator);
        return new PovGridImpl<>(pov, grid);
    }

    /**
     * Reads delimited text into a grid, one row per line.
     *
     * @param gridText the delimited text
     * @param separator the cell delimiter
     * @return the resulting grid
     */
    public Grid<String> read(String gridText, String separator) {
        String lines[] = gridText.split("\\r?\\n");

        Grid<String> grid = null;
        for (int row = 0; row < lines.length; row++) {
            String[] cells = split(lines[row], separator);
            if (grid == null) {
                grid = new HashMapGrid<>(lines.length, cells.length);
            }
            for (int col = 0; col < cells.length; col++) {
                String cell = cells[col];
                if (!cell.trim().isEmpty()) {
                    grid.setCell(row, col, cell);
                }
            }
        }
        return grid;
    }

    /**
     * Reads delimited text lines into a grid, one row per line.
     *
     * @param lines the delimited text lines
     * @param separator the cell delimiter
     * @return the resulting grid
     */
    public Grid<String> read(String[] lines, String separator) {
        Grid<String> grid = null;
        for (int row = 0; row < lines.length; row++) {
            String[] cells = split(lines[row], separator);
            if (grid == null) {
                grid = new HashMapGrid<>(lines.length, cells.length);
            }
            for (int col = 0; col < cells.length; col++) {
                String cell = cells[col];
                if (!cell.trim().isEmpty()) {
                    grid.setCell(row, col, cell);
                }
            }
        }
        return grid;
    }

    /**
     * Splits a line of delimited text into cells, trimming each cell's contents.
     *
     * @param text the line to split
     * @param separator the cell delimiter
     * @return the trimmed cells
     */
    public static String[] split(String text, String separator) {
        String[] items = StringUtils.delimitedListToStringArray(text, separator);
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null) {
                items[i] = items[i].trim();
            }
        }
        return items;
    }

}
