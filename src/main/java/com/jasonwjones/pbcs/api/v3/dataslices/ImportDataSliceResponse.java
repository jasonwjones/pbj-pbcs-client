package com.jasonwjones.pbcs.api.v3.dataslices;

import java.util.List;

/**
 * Represents the response payload for the "import data slice" REST endpoint.
 */
public class ImportDataSliceResponse {

    private int numAcceptedCells;

    // Boxed on purpose: a server that does not report this field at all is not the same as one
    // reporting zero, and an int cannot tell the two apart. Jackson only calls the setter when the
    // field is present, so null means "not reported" and a caller can decline to draw a conclusion.
    private Integer numUpdateCells;

    private int numRejectedCells;

    private List<String> rejectedCells;

    private List<RejectedCellDetails> rejectedCellsWithDetails;

    /**
     * Constructs an empty instance for deserialization.
     */
    public ImportDataSliceResponse() {
    }

    /**
     * Gets the number of cells accepted by the import.
     *
     * @return the accepted cell count
     */
    public int getNumAcceptedCells() {
        return numAcceptedCells;
    }

    /**
     * Sets the number of cells accepted by the import.
     *
     * @param numAcceptedCells the accepted cell count
     */
    public void setNumAcceptedCells(int numAcceptedCells) {
        this.numAcceptedCells = numAcceptedCells;
    }

    /**
     * Gets the number of cells the import actually changed in Essbase.
     *
     * <p>Not the same as the accepted count, and the difference is the useful part: a cell whose new
     * value equals its old one is accepted and not updated. A submit that accepts everything and
     * updates nothing has done exactly nothing, and says so here rather than looking like a success.
     *
     * @return the updated cell count
     */
    public int getNumUpdateCells() {
        return numUpdateCells == null ? 0 : numUpdateCells;
    }

    /**
     * Whether the server reported an updated cell count at all.
     *
     * <p>Not every pod returns {@code numUpdateCells}, and one that does not is indistinguishable
     * from one reporting zero unless this is asked. Treating an unreported count as zero would call
     * a perfectly good import a failure.
     *
     * @return true if the count was reported, false otherwise
     */
    public boolean isUpdateCountReported() {
        return numUpdateCells != null;
    }

    /**
     * Sets the number of cells the import actually changed.
     *
     * @param numUpdateCells the updated cell count
     */
    public void setNumUpdateCells(Integer numUpdateCells) {
        this.numUpdateCells = numUpdateCells;
    }

    /**
     * Gets the number of cells rejected by the import.
     *
     * @return the rejected cell count
     */
    public int getNumRejectedCells() {
        return numRejectedCells;
    }

    /**
     * Sets the number of cells rejected by the import.
     *
     * @param numRejectedCells the rejected cell count
     */
    public void setNumRejectedCells(int numRejectedCells) {
        this.numRejectedCells = numRejectedCells;
    }

    /**
     * Gets the rejected cells, if requested via {@link ImportDataSlice.CustomParams#isIncludeRejectedCells()}.
     *
     * @return the rejected cells, may be null if not requested/returned
     */
    public List<String> getRejectedCells() {
        return rejectedCells;
    }

    /**
     * Sets the rejected cells.
     *
     * @param rejectedCells the rejected cells
     */
    public void setRejectedCells(List<String> rejectedCells) {
        this.rejectedCells = rejectedCells;
    }

    /**
     * Gets the rejected cells with details, if requested via
     * {@link ImportDataSlice.CustomParams#isIncludeRejectedCellsWithDetails()}.
     *
     * @return the rejected cells with details, may be null if not requested/returned
     */
    public List<RejectedCellDetails> getRejectedCellsWithDetails() {
        return rejectedCellsWithDetails;
    }

    /**
     * Sets the rejected cells with details.
     *
     * @param rejectedCellsWithDetails the rejected cells with details
     */
    public void setRejectedCellsWithDetails(List<RejectedCellDetails> rejectedCellsWithDetails) {
        this.rejectedCellsWithDetails = rejectedCellsWithDetails;
    }

    /**
     * Details explaining why a single cell was rejected by an import.
     */
    public static class RejectedCellDetails {

        private List<String> memberNames;

        private List<String> readOnlyReasons;

        private List<String> otherReasons;

        /**
         * Constructs an empty instance for deserialization.
         */
        public RejectedCellDetails() {
        }

        /**
         * Gets the member names identifying the rejected cell.
         *
         * @return the member names
         */
        public List<String> getMemberNames() {
            return memberNames;
        }

        /**
         * Sets the member names identifying the rejected cell.
         *
         * @param memberNames the member names
         */
        public void setMemberNames(List<String> memberNames) {
            this.memberNames = memberNames;
        }

        /**
         * Gets the reasons the cell was read-only.
         *
         * @return the read-only reasons
         */
        public List<String> getReadOnlyReasons() {
            return readOnlyReasons;
        }

        /**
         * Sets the reasons the cell was read-only.
         *
         * @param readOnlyReasons the read-only reasons
         */
        public void setReadOnlyReasons(List<String> readOnlyReasons) {
            this.readOnlyReasons = readOnlyReasons;
        }

        /**
         * Gets any other reasons the cell was rejected.
         *
         * @return the other reasons
         */
        public List<String> getOtherReasons() {
            return otherReasons;
        }

        /**
         * Sets other reasons the cell was rejected.
         *
         * @param otherReasons the other reasons
         */
        public void setOtherReasons(List<String> otherReasons) {
            this.otherReasons = otherReasons;
        }

    }

}
