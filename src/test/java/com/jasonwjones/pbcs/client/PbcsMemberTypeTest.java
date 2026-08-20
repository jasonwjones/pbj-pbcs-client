package com.jasonwjones.pbcs.client;

import org.junit.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class PbcsMemberTypeTest {

    @Test
    public void fromDimTypeMapsKnownSystemDimensionTypes() {
        Map<String, PbcsMemberType> expected = Map.of(
                "Account", PbcsMemberType.ACCOUNT,
                "Period", PbcsMemberType.TIME_PERIOD,
                "Year", PbcsMemberType.YEAR,
                "Scenario", PbcsMemberType.SCENARIO,
                "Version", PbcsMemberType.VERSION,
                "Currency", PbcsMemberType.CURRENCY,
                "Entity", PbcsMemberType.ENTITY,
                "Custom", PbcsMemberType.USER_DEFINED,
                "Attribute Dimension", PbcsMemberType.ATTRIBUTE);

        for (Map.Entry<String, PbcsMemberType> entry : expected.entrySet()) {
            assertThat(PbcsMemberType.fromDimType(entry.getKey()), is(entry.getValue()));
        }
    }

    @Test
    public void fromDimTypeReturnsUnknownForUnrecognizedString() {
        assertThat(PbcsMemberType.fromDimType("SomeFutureDimensionType"), is(PbcsMemberType.UNKNOWN));
    }

    @Test
    public void fromDimTypeReturnsUnknownForNull() {
        assertThat(PbcsMemberType.fromDimType(null), is(PbcsMemberType.UNKNOWN));
    }

}
