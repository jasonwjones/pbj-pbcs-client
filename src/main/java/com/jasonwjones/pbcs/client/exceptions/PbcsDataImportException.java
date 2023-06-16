package com.jasonwjones.pbcs.client.exceptions;

import com.jasonwjones.pbcs.api.v3.dataslices.ImportDataSliceResponse;

/**
 * Thrown when there are rejected cells during a data import operation and the import options have been configured to
 * throw an exception on rejected cells.
 */
public class PbcsDataImportException extends PbcsClientException {

    private final ImportDataSliceResponse response;

    public PbcsDataImportException(ImportDataSliceResponse response) {
        super(String.format("Failed to import %d cells (successful: %s)", response.getNumRejectedCells(), response.getNumAcceptedCells()));
        this.response = response;
    }

    public int getNumRejectedCells() {
        return response.getNumRejectedCells();
    }

    public int getNumAcceptedCells() {
        return response.getNumAcceptedCells();
    }

}