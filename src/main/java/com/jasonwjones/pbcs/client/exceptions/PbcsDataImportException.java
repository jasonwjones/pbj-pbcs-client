package com.jasonwjones.pbcs.client.exceptions;

import com.jasonwjones.pbcs.api.v3.dataslices.ImportDataSliceResponse;

import java.util.ArrayList;
import java.util.List;

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
        super(describe(response));
        this.response = response;
    }

    /**
     * Says why the cells were rejected, and not merely how many were.
     *
     * <p>The import asks for {@code IncludeRejectedCells} and {@code IncludeRejectedCellsWithDetails},
     * so the server has already said which intersection it refused and why - read-only, no such
     * member, no access. Reporting only the count threw that away and left the caller with a number
     * and nothing to do about it.
     *
     * @param response the import response
     * @return a message naming the rejected cells and their reasons, as far as the server gave them
     */
    private static String describe(ImportDataSliceResponse response) {
        StringBuilder message = new StringBuilder(String.format("Failed to import %d cells (successful: %s)",
                response.getNumRejectedCells(), response.getNumAcceptedCells()));
        List<ImportDataSliceResponse.RejectedCellDetails> details = response.getRejectedCellsWithDetails();
        if (details != null && !details.isEmpty()) {
            for (ImportDataSliceResponse.RejectedCellDetails cell : details) {
                message.append("\n\n").append(join(cell.getMemberNames(), " -> "));
                String reasons = reasonsOf(cell);
                if (!reasons.isEmpty()) {
                    message.append("\n").append(reasons);
                }
            }
        } else if (response.getRejectedCells() != null && !response.getRejectedCells().isEmpty()) {
            // Without the details, the coordinates are still better than the count alone.
            for (String cell : response.getRejectedCells()) {
                message.append("\n\n").append(cell);
            }
        }
        return message.toString();
    }

    /** A cell's reasons, read-only ones first, since that is the usual answer. */
    private static String reasonsOf(ImportDataSliceResponse.RejectedCellDetails cell) {
        List<String> reasons = new ArrayList<>();
        if (cell.getReadOnlyReasons() != null) {
            reasons.addAll(cell.getReadOnlyReasons());
        }
        if (cell.getOtherReasons() != null) {
            reasons.addAll(cell.getOtherReasons());
        }
        return join(reasons, "\n");
    }

    private static String join(List<String> parts, String separator) {
        if (parts == null || parts.isEmpty()) {
            return "";
        }
        StringBuilder joined = new StringBuilder();
        for (String part : parts) {
            if (joined.length() > 0) {
                joined.append(separator);
            }
            joined.append(part);
        }
        return joined.toString();
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

    /**
     * Gets the whole import response, including whatever the server said about each rejected cell.
     *
     * @return the import response
     */
    public ImportDataSliceResponse getResponse() {
        return response;
    }

}