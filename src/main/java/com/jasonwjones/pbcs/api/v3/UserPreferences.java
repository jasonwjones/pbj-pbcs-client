package com.jasonwjones.pbcs.api.v3;

/**
 * Maps a subset of the user preferences payload returned by the REST API's user preferences endpoint.
 */
public class UserPreferences {

    //  "minPrecision": 0,
    //  "maxPrecision": 0,
    //  "thousandsSeparator": ",",
    //  "negativeStyle": 2,
    //  "showPUAlias": true,
    //  "currSymbol": "",
    //  "scale": 0,
    //  "decimalSeparator": ".",

    private Integer minPrecision;

    private Integer maxPrecision;

    private String thousandsSeparator;

    /**
     * Constructs an empty instance for deserialization.
     */
    public UserPreferences() {
    }

    /**
     * Gets the minimum display precision.
     *
     * @return the minimum precision
     */
    public Integer getMinPrecision() {
        return minPrecision;
    }

    /**
     * Sets the minimum display precision.
     *
     * @param minPrecision the minimum precision
     */
    public void setMinPrecision(Integer minPrecision) {
        this.minPrecision = minPrecision;
    }

    /**
     * Gets the maximum display precision.
     *
     * @return the maximum precision
     */
    public Integer getMaxPrecision() {
        return maxPrecision;
    }

    /**
     * Sets the maximum display precision.
     *
     * @param maxPrecision the maximum precision
     */
    public void setMaxPrecision(Integer maxPrecision) {
        this.maxPrecision = maxPrecision;
    }

    /**
     * Gets the thousands separator preference.
     *
     * @return the thousands separator
     */
    public String getThousandsSeparator() {
        return thousandsSeparator;
    }

    /**
     * Sets the thousands separator preference.
     *
     * @param thousandsSeparator the thousands separator
     */
    public void setThousandsSeparator(String thousandsSeparator) {
        this.thousandsSeparator = thousandsSeparator;
    }

    @Override
    public String toString() {
        return "UserPreferences{" +
                "minPrecision=" + minPrecision +
                ", maxPrecision=" + maxPrecision +
                ", thousandsSeparator='" + thousandsSeparator + '\'' +
                '}';
    }

}
