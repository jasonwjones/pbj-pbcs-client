package com.jasonwjones.pbcs.api.v3;

/**
 * Part of member info payload, <code>currencySettings</code> is a sibling to <code>name</code> and other base level
 * properties.
 *
 * <pre>
 * "currencySettings": {
 *   "precision": -1,
 *   "scale": 0,
 *   "symbol": "$",
 *   "reportingCurrency": true,
 *   "thousandsSeparator": "NONE",
 *   "decimalSeparator": "DOT",
 *   "negativeSign": "PREFIXED_MINUS",
 *   "negativeColor": "BLACK"
 * },
 * </pre>
 */
public class CurrencySettings {

    private Integer precision; // -1 when <None>, available choices are 0-10

    private Integer scale; // usually one, choices are 1, 10, 100, ... up to 1 with 9 zeros

    private String symbol; // required, default is $; many symbols available

    private Boolean reportingCurrency;

    private ThousandsSeparator thousandsSeparator;

    private DecimalSeparator decimalSeparator;

    private NegativeSign negativeSign;

    private NegativeColor negativeColor;

    public Integer getPrecision() {
        return precision;
    }

    public void setPrecision(Integer precision) {
        this.precision = precision;
    }

    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Boolean getReportingCurrency() {
        return reportingCurrency;
    }

    public void setReportingCurrency(Boolean reportingCurrency) {
        this.reportingCurrency = reportingCurrency;
    }

    public ThousandsSeparator getThousandsSeparator() {
        return thousandsSeparator;
    }

    public void setThousandsSeparator(ThousandsSeparator thousandsSeparator) {
        this.thousandsSeparator = thousandsSeparator;
    }

    public DecimalSeparator getDecimalSeparator() {
        return decimalSeparator;
    }

    public void setDecimalSeparator(DecimalSeparator decimalSeparator) {
        this.decimalSeparator = decimalSeparator;
    }

    public NegativeSign getNegativeSign() {
        return negativeSign;
    }

    public void setNegativeSign(NegativeSign negativeSign) {
        this.negativeSign = negativeSign;
    }

    public NegativeColor getNegativeColor() {
        return negativeColor;
    }

    public void setNegativeColor(NegativeColor negativeColor) {
        this.negativeColor = negativeColor;
    }

    public enum ThousandsSeparator {

        CURRENCY_SETTING, // default

        NONE,

        COMMA,

        DOT,

        SPACE

    }

    public enum DecimalSeparator {

        CURRENCY_SETTING, // default

        DOT,

        COMMA

    }

    public enum NegativeSign {

        CURRENCY_SETTING, // default

        PREFIXED_MINUS,

        SUFFIXED_MINUS,

        PARENTHESES

    }

    public enum NegativeColor {

        CURRENCY_SETTING, // default

        BLACK,

        RED

    }

}
