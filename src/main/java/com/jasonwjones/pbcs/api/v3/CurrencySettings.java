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

    /**
     * Constructs an empty instance for deserialization.
     */
    public CurrencySettings() {
    }

    /**
     * Gets the display precision, i.e. the number of decimal places. A value of -1 means &lt;None&gt;;
     * otherwise the available choices are 0-10.
     *
     * @return the precision
     */
    public Integer getPrecision() {
        return precision;
    }

    /**
     * Sets the display precision.
     *
     * @param precision the precision
     */
    public void setPrecision(Integer precision) {
        this.precision = precision;
    }

    /**
     * Gets the scale, i.e. the divisor applied before display (1, 10, 100, ... up to 1 followed by 9 zeros).
     *
     * @return the scale
     */
    public Integer getScale() {
        return scale;
    }

    /**
     * Sets the scale.
     *
     * @param scale the scale
     */
    public void setScale(Integer scale) {
        this.scale = scale;
    }

    /**
     * Gets the currency symbol, e.g. "$". Required; defaults to "$" if not otherwise specified.
     *
     * @return the currency symbol
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Sets the currency symbol.
     *
     * @param symbol the currency symbol
     */
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Whether this is the reporting currency.
     *
     * @return true if this is the reporting currency, false otherwise
     */
    public Boolean getReportingCurrency() {
        return reportingCurrency;
    }

    /**
     * Sets whether this is the reporting currency.
     *
     * @param reportingCurrency true if this is the reporting currency, false otherwise
     */
    public void setReportingCurrency(Boolean reportingCurrency) {
        this.reportingCurrency = reportingCurrency;
    }

    /**
     * Gets the thousands separator style.
     *
     * @return the thousands separator
     */
    public ThousandsSeparator getThousandsSeparator() {
        return thousandsSeparator;
    }

    /**
     * Sets the thousands separator style.
     *
     * @param thousandsSeparator the thousands separator
     */
    public void setThousandsSeparator(ThousandsSeparator thousandsSeparator) {
        this.thousandsSeparator = thousandsSeparator;
    }

    /**
     * Gets the decimal separator style.
     *
     * @return the decimal separator
     */
    public DecimalSeparator getDecimalSeparator() {
        return decimalSeparator;
    }

    /**
     * Sets the decimal separator style.
     *
     * @param decimalSeparator the decimal separator
     */
    public void setDecimalSeparator(DecimalSeparator decimalSeparator) {
        this.decimalSeparator = decimalSeparator;
    }

    /**
     * Gets the negative sign style.
     *
     * @return the negative sign style
     */
    public NegativeSign getNegativeSign() {
        return negativeSign;
    }

    /**
     * Sets the negative sign style.
     *
     * @param negativeSign the negative sign style
     */
    public void setNegativeSign(NegativeSign negativeSign) {
        this.negativeSign = negativeSign;
    }

    /**
     * Gets the color used to display negative values.
     *
     * @return the negative value color
     */
    public NegativeColor getNegativeColor() {
        return negativeColor;
    }

    /**
     * Sets the color used to display negative values.
     *
     * @param negativeColor the negative value color
     */
    public void setNegativeColor(NegativeColor negativeColor) {
        this.negativeColor = negativeColor;
    }

    /**
     * The available thousands separator styles.
     */
    public enum ThousandsSeparator {

        /**
         * Use the currency's default thousands separator.
         */
        CURRENCY_SETTING,

        /**
         * No thousands separator.
         */
        NONE,

        /**
         * Use a comma as the thousands separator.
         */
        COMMA,

        /**
         * Use a period/dot as the thousands separator.
         */
        DOT,

        /**
         * Use a space as the thousands separator.
         */
        SPACE

    }

    /**
     * The available decimal separator styles.
     */
    public enum DecimalSeparator {

        /**
         * Use the currency's default decimal separator.
         */
        CURRENCY_SETTING,

        /**
         * Use a period/dot as the decimal separator.
         */
        DOT,

        /**
         * Use a comma as the decimal separator.
         */
        COMMA

    }

    /**
     * The available styles for displaying a negative sign.
     */
    public enum NegativeSign {

        /**
         * Use the currency's default negative sign style.
         */
        CURRENCY_SETTING,

        /**
         * Display the negative sign before the value, e.g. "-100".
         */
        PREFIXED_MINUS,

        /**
         * Display the negative sign after the value, e.g. "100-".
         */
        SUFFIXED_MINUS,

        /**
         * Display negative values surrounded by parentheses, e.g. "(100)".
         */
        PARENTHESES

    }

    /**
     * The available colors for displaying negative values.
     */
    public enum NegativeColor {

        /**
         * Use the currency's default negative value color.
         */
        CURRENCY_SETTING,

        /**
         * Display negative values in black.
         */
        BLACK,

        /**
         * Display negative values in red.
         */
        RED

    }

}
