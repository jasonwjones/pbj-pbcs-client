package com.jasonwjones.pbcs.client.exceptions;

import com.jasonwjones.pbcs.api.v3.dataslices.ImportDataSliceResponse;

/**
 * Thrown when there are rejected cells during a data import operation and the import options have been configured to
 * throw an exception on rejected cells.
 */
public class PbcsDataImportException extends PbcsClientException {

    /**
     * The import response containing the rejected/accepted cell counts.
     */
    private final ImportDataSliceResponse response;

    /**
     * Constructs an instance from the given import response.
     *
     * @param response the import response containing the rejected/accepted cell counts
     */
    public PbcsDataImportException(ImportDataSliceResponse response) {
        super(String.format("Failed to import %d cells (successful: %s)", response.getNumRejectedCells(), response.getNumAcceptedCells()));
        this.response = response;
    }

    /**
     * Gets the number of cells that were rejected.
     *
     * @return the rejected cell count
     */
    public int getNumRejectedCells() {
        return response.getNumRejectedCells();
    }

    /**
     * Gets the number of cells that were accepted.
     *
     * @return the accepted cell count
     */
    public int getNumAcceptedCells() {
        return response.getNumAcceptedCells();
    }

}