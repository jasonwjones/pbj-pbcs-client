package com.jasonwjones.pbcs.matchers;

import com.jasonwjones.pbcs.client.Grid;
import com.jasonwjones.pbcs.client.impl.grid.DataSliceGrid;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class DataSliceGridMatcher extends TypeSafeMatcher<DataSliceGrid> {

    private final int rows;

    private final int columns;

    public DataSliceGridMatcher(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
    }

    @Override
    protected boolean matchesSafely(DataSliceGrid item) {
        return item.getRows() == rows && item.getColumns() == columns;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("data slice with dimensions " + rows + "x" + columns);
    }

    public static Matcher<DataSliceGrid> hasDimensions(int rows, int columns) {
        return new DataSliceGridMatcher(rows, columns);
    }

    public static Matcher<DataSliceGrid> hasDimensions(Grid<?> grid) {
        return new DataSliceGridMatcher(grid.getRows(), grid.getColumns());
    }

}