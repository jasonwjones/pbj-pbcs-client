package com.jasonwjones.pbcs.client;

/**
 * Models the application type. This corresponds to the <code>appType</code> that gets returned from an application
 * listing, as opposed to the <code>type</code> that seems to always come back as <code>HP</code>.
 */
public enum PbcsAppType {

    /**
     * The app is a Planning application.
     */
    PLANNING("EPBCS"),

    /**
     * The app is an FCCS application.
     */
    CONSOLIDATION("FCCS");

    private final String code;

    PbcsAppType(String code) {
        this.code = code;
    }

    /**
     * The code that is used in the REST API to define the app.
     *
     * @return the code for this app type
     */
    public String getCode() {
        return code;
    }

}