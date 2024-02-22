package com.jasonwjones.pbcs.util;

import com.jasonwjones.pbcs.client.Grid;
import com.jasonwjones.pbcs.client.impl.HashMapGrid;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TextGridReader {

    public Grid<String> read(String resourceName) throws IOException {
        Resource resource = new ClassPathResource(resourceName);
        return read(resource.getInputStream(), ",");
    }

    public Grid<String> read(InputStream inputStream, String separator) throws IOException {
        String gridText = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
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