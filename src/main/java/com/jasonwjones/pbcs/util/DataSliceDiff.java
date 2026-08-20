package com.jasonwjones.pbcs.util;

import com.jasonwjones.pbcs.api.v3.dataslices.DataSlice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Generates a difference between two data slices. Can be used for "native" before/after audit logging.
 */
public class DataSliceDiff {

    private static final Logger logger = LoggerFactory.getLogger(DataSliceDiff.class);

    private DataSliceDiff() {}

    /**
     * Computes the set of cells that changed between two data slices with matching row/column shape,
     * keyed by the POV (including row/column headers) of the changed cell.
     *
     * @param first the "before" data slice
     * @param second the "after" data slice
     * @return the changes, keyed by the full POV of each changed cell
     * @throws IllegalArgumentException if the two data slices do not have the same number of rows
     */
    public static Map<Set<String>, ValChange> diff(DataSlice first, DataSlice second) {
        Objects.requireNonNull(first);
        Objects.requireNonNull(second);

        if (first.getRows().size() != second.getRows().size()) {
            throw new IllegalArgumentException("Row count mismatch: " + first.getRows().size() + " != " + second.getRows().size());
        }

        Map<Set<String>, ValChange> changes = new HashMap<>();

        for (int row = 0; row < first.getRows().size(); row++) {
            DataSlice.HeaderDataRow headerDataRow = first.getRows().get(row);
            DataSlice.HeaderDataRow headerDataRow2 = second.getRows().get(row);

            if (!headerDataRow.getHeaders().equals(headerDataRow2.getHeaders())) {
                logger.warn("Header data rows do not match");
            } else {
                for (int col = 0; col < headerDataRow.getData().size(); col++) {
                    String previousValue = headerDataRow.getData().get(col);
                    String currentValue = headerDataRow2.getData().get(col);
                    if (!Objects.equals(previousValue, currentValue)) {
                        Set<String> pov = new HashSet<>(first.getPov());
                        pov.addAll(first.getColumns().get(col));
                        pov.addAll(headerDataRow.getHeaders());
                        changes.put(pov, new ValChange(previousValue, currentValue));
                    }
                }
            }
        }
        return changes;
    }

    /**
     * Represents a single cell's before and after values.
     */
    public static class ValChange {

        private final String before;

        private final String after;

        /**
         * Constructs an instance with the given before and after values.
         *
         * @param before the value before the change
         * @param after the value after the change
         */
        public ValChange(String before, String after) {
            this.before = before;
            this.after = after;
        }

        /**
         * Gets the value after the change.
         *
         * @return the after value
         */
        public String getAfter() {
            return after;
        }

        /**
         * Gets the value before the change.
         *
         * @return the before value
         */
        public String getBefore() {
            return before;
        }

        @Override
        public String toString() {
            return before + " --> " + after;
        }

    }

}