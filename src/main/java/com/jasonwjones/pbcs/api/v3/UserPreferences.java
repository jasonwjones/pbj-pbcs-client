package com.jasonwjones.pbcs.api.v3;

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

    public Integer getMinPrecision() {
        return minPrecision;
    }

    public void setMinPrecision(Integer minPrecision) {
        this.minPrecision = minPrecision;
    }

    public Integer getMaxPrecision() {
        return maxPrecision;
    }

    public void setMaxPrecision(Integer maxPrecision) {
        this.maxPrecision = maxPrecision;
    }

    public String getThousandsSeparator() {
        return thousandsSeparator;
    }

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