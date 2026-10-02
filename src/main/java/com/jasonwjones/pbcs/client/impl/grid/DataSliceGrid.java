package com.jasonwjones.pbcs.client.impl.grid;

import com.jasonwjones.pbcs.api.v3.dataslices.DataSlice;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.client.PovGrid;
import com.jasonwjones.pbcs.client.impl.PovGridImpl;

import java.util.ArrayList;
import java.util.Collections;
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
                DataSlice.HeaderDataRow dataRow = dataSlice.getRows().get(row - topRows);
                int dataIndex = column - leftCols;
                return new DataCellImpl(dataRow.getData().get(dataIndex), cellNotes(dataRow, dataIndex), supportingDetail(dataRow, dataIndex));
            }
        }
    }

    // A row has no notes list at all unless planning data was exported and one of its cells has a note.
    private static List<String> cellNotes(DataSlice.HeaderDataRow dataRow, int dataIndex) {
        List<List<DataSlice.CellNote>> rowNotes = dataRow.getCellNotes();
        if (rowNotes == null || dataIndex >= rowNotes.size() || rowNotes.get(dataIndex) == null) {
            return Collections.emptyList();
        }
        List<String> contents = new ArrayList<>();
        for (DataSlice.CellNote note : rowNotes.get(dataIndex)) {
            if (note != null) contents.add(note.getContents());
        }
        return Collections.unmodifiableList(contents);
    }

    // Copies, so that a caller editing a line does not quietly edit the data slice behind this grid.
    private static List<DataSlice.SupportingDetail> supportingDetail(DataSlice.HeaderDataRow dataRow, int dataIndex) {
        List<DataSlice.SupportingDetailWrapper> rowDetail = dataRow.getSupportingDetail();
        if (rowDetail == null || dataIndex >= rowDetail.size() || rowDetail.get(dataIndex) == null || rowDetail.get(dataIndex).getItems() == null) {
            return Collections.emptyList();
        }
        List<DataSlice.SupportingDetail> lines = new ArrayList<>();
        for (DataSlice.SupportingDetail line : rowDetail.get(dataIndex).getItems()) {
            DataSlice.SupportingDetail copy = new DataSlice.SupportingDetail(line.getLabel(), line.getOperator(), line.getValue(), line.getGeneration());
            copy.setPosition(line.getPosition());
            lines.add(copy);
        }
        return Collections.unmodifiableList(lines);
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
     * A {@link Cell} that holds a data value, along with any cell notes and supporting detail on that
     * intersection.
     */
    public interface DataCell extends Cell {

        /**
         * Gets the cell notes (shown as "comments" in the Planning UI) on this cell. Notes are only exported
         * when the retrieve was made with {@link PbcsPlanType.RetrieveOptions#isExportPlanningData()} set, so
         * this is always empty for a grid retrieved without it.
         *
         * @return the contents of each note on this cell, in the order the server returned them; empty if
         * there are none or they were not exported
         */
        List<String> getCellNotes();

        /**
         * Gets the supporting detail lines on this cell, in position order. Like cell notes, supporting
         * detail is only exported when the retrieve was made with
         * {@link PbcsPlanType.RetrieveOptions#isExportPlanningData()} set.
         *
         * @return copies of the cell's supporting detail lines; empty if there are none or they were not
         * exported
         */
        List<DataSlice.SupportingDetail> getSupportingDetail();

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

    private static class DataCellImpl extends AnyCell implements DataCell {

        private final List<String> cellNotes;

        private final List<DataSlice.SupportingDetail> supportingDetail;

        private DataCellImpl(String value, List<String> cellNotes, List<DataSlice.SupportingDetail> supportingDetail) {
            super(value);
            this.cellNotes = cellNotes;
            this.supportingDetail = supportingDetail;
        }

        @Override
        public CellType getType() {
            return CellType.DATA;
        }

        @Override
        public List<String> getCellNotes() {
            return cellNotes;
        }

        @Override
        public List<DataSlice.SupportingDetail> getSupportingDetail() {
            return supportingDetail;
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