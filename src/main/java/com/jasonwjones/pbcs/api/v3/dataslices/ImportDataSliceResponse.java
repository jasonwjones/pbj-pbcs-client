package com.jasonwjones.pbcs.api.v3.dataslices;

import java.util.List;

public class ImportDataSliceResponse {

    private int numAcceptedCells;

    private int numRejectedCells;

    private List<String> rejectedCells;

    private List<RejectedCellDetails> rejectedCellsWithDetails;

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

    public List<RejectedCellDetails> getRejectedCellsWithDetails() {
        return rejectedCellsWithDetails;
    }

    public void setRejectedCellsWithDetails(List<RejectedCellDetails> rejectedCellsWithDetails) {
        this.rejectedCellsWithDetails = rejectedCellsWithDetails;
    }

    public static class RejectedCellDetails {

        private List<String> memberNames;

        private List<String> readOnlyReasons;

        private List<String> otherReasons;

        public List<String> getMemberNames() {
            return memberNames;
        }

        public void setMemberNames(List<String> memberNames) {
            this.memberNames = memberNames;
        }

        public List<String> getReadOnlyReasons() {
            return readOnlyReasons;
        }

        public void setReadOnlyReasons(List<String> readOnlyReasons) {
            this.readOnlyReasons = readOnlyReasons;
        }

        public List<String> getOtherReasons() {
            return otherReasons;
        }

        public void setOtherReasons(List<String> otherReasons) {
            this.otherReasons = otherReasons;
        }

    }

}