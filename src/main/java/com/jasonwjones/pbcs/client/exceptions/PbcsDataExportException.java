package com.jasonwjones.pbcs.client.exceptions;

import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.client.PovGrid;

/**
 * Thrown when a retrieve/export data slice operation fails, such as in
 * {@link com.jasonwjones.pbcs.client.PbcsPlanType#retrieve(PovGrid, PbcsPlanType.RetrieveOptions)}.
 */
public class PbcsDataExportException extends PbcsClientException {

    private final transient PovGrid<String> grid;

    /**
     * Create this exception.
     *
     * @param grid the grid that was being retrieved when the exception occurred
     * @param cause the underlying exception
     */
    public PbcsDataExportException(PovGrid<String> grid, Throwable cause) {
        super("Error retrieving grid: " + cause.getMessage(), cause);
        this.grid = grid;
    }

    /**
     * Returns the grid that was being retrieved when the underlying exception was thrown.
     *
     * @return the grid
     */
    public PovGrid<String> getGrid() {
        return grid;
    }

}