package com.jasonwjones.pbcs.client.impl.grid;

import com.jasonwjones.pbcs.api.v3.dataslices.DataSlice;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.client.PovGrid;
import com.jasonwjones.pbcs.client.impl.PovGridImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * A {@link PovGrid} view over a {@link DataSlice}, exposing its POV, top axis, left axis, and data cells
 * as a two-dimensional grid.
 */
public class DataSliceGrid implements PovGrid<DataSliceGrid.Cell> {

    private final PbcsPlanType planType;

    private final DataSlice dataSlice;

    private final List<Cell> povCells;

    private final int povMemberCount;

    private final int rows;

    private final int columns;

    private final int topRows;

    private final int leftCols;

    /**
     * A shared blank cell instance, used for the region where the top and left axes meet.
     */
    public static final Cell BLANK = new BlankCell();

    private final ConcurrentMap<Integer, String> axisDimensionLookups = new ConcurrentHashMap<>();

    /**
     * Constructs a grid view over the given data slice, inferring the left column count from the data slice's
     * first row.
     *
     * @param planType the plan type the data slice was retrieved from
     * @param dataSlice the data slice to wrap
     */
    public DataSliceGrid(PbcsPlanType planType, DataSlice dataSlice) {
        this(planType, dataSlice, dataSlice.getRows().get(0).getHeaders().size());
    }

    /**
     * Constructs a grid view over the given data slice, using the given left column count hint when the
     * data slice has no rows to infer it from.
     *
     * @param planType the plan type the data slice was retrieved from
     * @param dataSlice the data slice to wrap
     * @param leftColsHint the left column count to use if the data slice has no rows
     */
    public DataSliceGrid(PbcsPlanType planType, DataSlice dataSlice, int leftColsHint) {
        this.planType = planType;
        this.dataSlice = dataSlice;
        this.povMemberCount = dataSlice.getPov().size();

        this.povCells = new ArrayList<>();
        for (int povIndex = 0; povIndex < dataSlice.getPov().size(); povIndex++) {
            String povMember = dataSlice.getPov().get(povIndex);
            povCells.add(new MemberCellImpl(povMember, povIndex));
        }

        this.rows = rows(dataSlice);
        this.topRows = dataSlice.getColumns().size();
        this.leftCols = !dataSlice.getRows().isEmpty()
                ? dataSlice.getRows().get(0).getHeaders().size()
                : leftColsHint;
        this.columns = leftCols + dataSlice.getColumns().get(0).size();
    }

    private static int rows(DataSlice dataSlice) {
        return dataSlice.getColumns().size() + dataSlice.getRows().size();
    }

    private static int columns(DataSlice dataSlice) {
        return dataSlice.getRows().get(0).getHeaders().size() + dataSlice.getRows().get(0).getData().size();
    }

    /**
     * Gets the plan type this data slice was retrieved from.
     *
     * @return the plan type
     */
    public PbcsPlanType getPlanType() {
        return planType;
    }

    /**
     * Gets the underlying data slice wrapped by this grid.
     *
     * @return the data slice
     */
    public DataSlice getDataSlice() {
        return dataSlice;
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
    public List<Cell> getPov() {
        return povCells;
    }

    @Override
    public <T> PovGrid<T> copyOf(Function<Cell, T> conversion) {
        return PovGridImpl.copy(this, conversion);
    }

    /**
     * Gets a cell from the grid represented by this data slice, not counting a row for the POV.
     *
     * @param row the index of the row to get
     * @param column the index of the column to get
     * @return a cell for the given intersection
     */
    @Override
    public Cell getCell(int row, int column) {
        if (row < topRows) {
            if (column < leftCols) { // blank
                return BLANK;
            } else { // header
                int axisPosition = povMemberCount + row;
                String member = dataSlice.getColumns().get(row).get(column - leftCols);
                return new MemberCellImpl(member, axisPosition);
            }
        } else {
            if (column < leftCols) { // left
                int axisPosition = povMemberCount + dataSlice.getColumns().size() + column;
                return new MemberCellImpl(dataSlice.getRows().get(row - topRows).getHeaders().get(column), axisPosition);
            } else { // data
                return new DataCell(dataSlice.getRows().get(row - topRows).getData().get(column - leftCols));
            }
        }
    }

    @Override
    public void setCell(int row, int column, Cell value) {
        throw new UnsupportedOperationException();
    }

    /**
     * Prints this grid to {@link System#out} for debugging purposes.
     */
    public void print() {
        System.out.println("POV: " + String.join(", ", dataSlice.getPov()));
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                Cell cell = getCell(row, col);
                String contents = cell.getValue() == null ? "" : cell.getValue();
                System.out.printf("%30s", contents);
            }
            System.out.println();
        }
    }

    /**
     * Returns the number of rows in the "top" or "columns" axis. In other words, this is the count of the number of
     * dimensions in the top region of the returned grid (below the POV), which will always be at least 1.
     *
     * @return number of rows in the "top" axis
     * @see #getLeftCols()
     */
    public int getTopRows() {
        return topRows;
    }

    /**
     * Returns the number of columns in the "left" or "rows" axis. In other words, this is the count of the number of
     * dimensions in the left region of the returned grid, which will always be at least 1.
     *
     * @return the number of columns in the "left" axis
     * @see #getTopRows()
     */
    public int getLeftCols() {
        return leftCols;
    }

    /**
     * A single cell of a {@link DataSliceGrid}, which may be a member header, a data value, or blank.
     */
    public interface Cell {

        /**
         * Gets the type of this cell.
         *
         * @return the cell type
         */
        CellType getType();

        /**
         * Gets the value of this cell: the member name for a member cell, the data value for a data cell, or
         * null for a blank cell.
         *
         * @return the cell value
         */
        String getValue(); // TODO: should we just use toString???

    }

    /**
     * A {@link Cell} that represents a member on one of the grid's axes.
     */
    public interface MemberCell extends Cell {

        /**
         * Gets the name of the dimension this member belongs to.
         *
         * @return the dimension name
         */
        String getDimensionName();

        /**
         * Gets the number of the dimension this member belongs to.
         *
         * @return the dimension number
         */
        int getDimensionNumber();

    }

    /**
     * The kinds of cells that can appear in a {@link DataSliceGrid}.
     */
    public enum CellType {

        /**
         * A cell representing a member on one of the grid's axes.
         */
        MEMBER,

        /**
         * A cell containing a data value.
         */
        DATA,

        /**
         * A cell in the blank region where the top and left axes meet.
         */
        BLANK

    }

    private abstract static class AnyCell implements Cell {

        protected final String value;

        private AnyCell(String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return getValue();
        }

    }

    private class MemberCellImpl extends AnyCell implements MemberCell {

        private final int axisPosition;

        private MemberCellImpl(String value, int axisPosition) {
            super(value);
            this.axisPosition = axisPosition;
        }

        @Override
        public CellType getType() {
            return CellType.MEMBER;
        }

        @Override
        public String getDimensionName() {
            return axisDimensionLookups.computeIfAbsent(axisPosition, integer -> planType.getMember(value).getDimensionName());
        }

        public int getDimensionNumber() {
            return planType.getDimension(getDimensionName()).getNumber();
        }

    }

    private static class DataCell extends AnyCell {

        private DataCell(String value) {
            super(value);
        }

        @Override
        public CellType getType() {
            return CellType.DATA;
        }

    }

    private static class BlankCell implements Cell {

        @Override
        public CellType getType() {
            return CellType.BLANK;
        }

        @Override
        public String getValue() {
            return null;
        }

        @Override
        public String toString() {
            return getValue();
        }

    }

}