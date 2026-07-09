package com.jasonwjones.pbcs.client.impl.export;

public class ExportPrintOptions {

    private int headerWidth = 30;

    private int dataWidth = 12;

    public int getDataWidth() {
        return dataWidth;
    }

    public void setDataWidth(int dataWidth) {
        this.dataWidth = dataWidth;
    }

    public int getHeaderWidth() {
        return headerWidth;
    }

    public void setHeaderWidth(int headerWidth) {
        this.headerWidth = headerWidth;
    }

}