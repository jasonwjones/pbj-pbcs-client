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

    public static class ValChange {

        private final String before;

        private final String after;

        public ValChange(String before, String after) {
            this.before = before;
            this.after = after;
        }

        public String getAfter() {
            return after;
        }

        public String getBefore() {
            return before;
        }

        @Override
        public String toString() {
            return before + " --> " + after;
        }

    }

}