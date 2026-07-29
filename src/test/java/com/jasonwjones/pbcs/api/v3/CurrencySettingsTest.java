package com.jasonwjones.pbcs.api.v3;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class CurrencySettingsTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void deserializeCurrencySettingsFromMemberProperties() throws Exception {
        String json = """
                {
                  "name": "USD",
                  "currencySettings": {
                    "precision": -1,
                    "scale": 0,
                    "symbol": "$",
                    "reportingCurrency": true,
                    "thousandsSeparator": "NONE",
                    "decimalSeparator": "DOT",
                    "negativeSign": "PREFIXED_MINUS",
                    "negativeColor": "BLACK"
                  }
                }
                """;

        PbcsMemberPropertiesImpl memberProperties = objectMapper.readValue(json, PbcsMemberPropertiesImpl.class);
        CurrencySettings currencySettings = memberProperties.getCurrencySettings();

        assertThat(currencySettings, is(notNullValue()));
        assertThat(currencySettings.getPrecision(), is(-1));
        assertThat(currencySettings.getScale(), is(0));
        assertThat(currencySettings.getSymbol(), is("$"));
        assertThat(currencySettings.getReportingCurrency(), is(true));
        assertThat(currencySettings.getThousandsSeparator(), is(CurrencySettings.ThousandsSeparator.NONE));
        assertThat(currencySettings.getDecimalSeparator(), is(CurrencySettings.DecimalSeparator.DOT));
        assertThat(currencySettings.getNegativeSign(), is(CurrencySettings.NegativeSign.PREFIXED_MINUS));
        assertThat(currencySettings.getNegativeColor(), is(CurrencySettings.NegativeColor.BLACK));
    }

}
