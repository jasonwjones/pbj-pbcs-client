package com.jasonwjones.pbcs.api.v3.dataslices;

import java.util.List;

public class ImportDataSliceResponse {

    private int numAcceptedCells;

    private int numRejectedCells;

    private List<String> rejectedCells;

    private List<String> rejectedCellsWithDetails;

    public int getNumAcceptedCells() {
        return numAcceptedCells;
    }

    public void setNumAcceptedCells(int numAcceptedCells) {
        this.numAcceptedCells = numAcceptedCells;
    }

    public int getNumRejectedCells() {
        return numRejectedCells;
    }

    public void setNumRejectedCells(int numRejectedCells) {
        this.numRejectedCells = numRejectedCells;
    }

    public List<String> getRejectedCells() {
        return rejectedCells;
    }

    public void setRejectedCells(List<String> rejectedCells) {
        this.rejectedCells = rejectedCells;
    }

    public List<String> getRejectedCellsWithDetails() {
        return rejectedCellsWithDetails;
    }

    public void setRejectedCellsWithDetails(List<String> rejectedCellsWithDetails) {
        this.rejectedCellsWithDetails = rejectedCellsWithDetails;
    }

}