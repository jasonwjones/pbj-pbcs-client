package com.jasonwjones.pbcs.client.impl.grid;

import com.jasonwjones.pbcs.api.v3.dataslices.DataSlice;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.client.PovGrid;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DataSliceGrid implements PovGrid<DataSliceGrid.Cell> {

    private final PbcsPlanType planType;

    private final DataSlice dataSlice;

    private final List<Cell> povCells;

    private final int povMemberCount;

    private final int rows;

    private final int columns;

    private final int topRows;

    private final int leftCols;

    private final Cell BLANK = new BlankCell();

    private final ConcurrentMap<Integer, String> axisDimensionLookups = new ConcurrentHashMap<>();

    public DataSliceGrid(PbcsPlanType planType, DataSlice dataSlice) {
        this.planType = planType;
        this.dataSlice = dataSlice;
        this.povMemberCount = dataSlice.getPov().size();

        this.povCells = new ArrayList<>();
        for (int povIndex = 0; povIndex < dataSlice.getPov().size(); povIndex++) {
            String povMember = dataSlice.getPov().get(povIndex);
            povCells.add(new MemberCellImpl(povMember, povIndex));
        }

        this.rows = rows(dataSlice);
        this.columns = columns(dataSlice);
        this.topRows = dataSlice.getColumns().size();
        this.leftCols = dataSlice.getRows().get(0).getHeaders().size();
    }

    private static int rows(DataSlice dataSlice) {
        return dataSlice.getColumns().size() + dataSlice.getRows().size();
    }

    private static int columns(DataSlice dataSlice) {
        return dataSlice.getRows().get(0).getHeaders().size() + dataSlice.getRows().get(0).getData().size();
    }

    public PbcsPlanType getPlanType() {
        return planType;
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

    public int getTopRows() {
        return topRows;
    }

    public int getLeftCols() {
        return leftCols;
    }

    public interface Cell {

        CellType getType();

        String getValue();

    }

    public interface MemberCell extends Cell {

        String getDimensionName();

        int getDimensionNumber();

    }

    public enum CellType {

        MEMBER,
        DATA,
        BLANK

    }

    private static class AnyCell implements Cell {

        protected final String value;

        private AnyCell(String value) {
            this.value = value;
        }

        @Override
        public CellType getType() {
            return null;
        }

        @Override
        public String getValue() {
            return value;
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

    }


}