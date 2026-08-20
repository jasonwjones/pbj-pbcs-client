package com.jasonwjones.pbcs.client.impl.export;

/**
 * Column width options used when printing an export to a fixed-width text format.
 */
public class ExportPrintOptions {

    private int headerWidth = 30;

    private int dataWidth = 12;

    /**
     * Constructs an instance with the default header and data widths.
     */
    public ExportPrintOptions() {
    }

    /**
     * Gets the width used for data columns.
     *
     * @return the data column width
     */
    public int getDataWidth() {
        return dataWidth;
    }

    /**
     * Sets the width used for data columns.
     *
     * @param dataWidth the data column width
     */
    public void setDataWidth(int dataWidth) {
        this.dataWidth = dataWidth;
    }

    /**
     * Gets the width used for header columns.
     *
     * @return the header column width
     */
    public int getHeaderWidth() {
        return headerWidth;
    }

    /**
     * Sets the width used for header columns.
     *
     * @param headerWidth the header column width
     */
    public void setHeaderWidth(int headerWidth) {
        this.headerWidth = headerWidth;
    }

}
