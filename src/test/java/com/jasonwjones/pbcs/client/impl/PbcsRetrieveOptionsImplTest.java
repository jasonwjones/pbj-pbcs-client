package com.jasonwjones.pbcs.client.impl;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class PbcsRetrieveOptionsImplTest {

    @Test
    public void suppressMissingRowsAndColumnsAreDisabledByDefault() {
        PbcsRetrieveOptionsImpl options = new PbcsRetrieveOptionsImpl();

        assertThat(options.isSuppressMissingRows(), is(false));
        assertThat(options.isSuppressMissingColumns(), is(false));
    }

    @Test
    public void suppressMissingRowsCanBeEnabled() {
        PbcsRetrieveOptionsImpl options = new PbcsRetrieveOptionsImpl();

        options.setSuppressMissing(true);

        assertThat(options.isSuppressMissingRows(), is(true));
        assertThat(options.isSuppressMissingColumns(), is(false));
    }

    @Test
    public void suppressMissingColumnsCanBeEnabled() {
        PbcsRetrieveOptionsImpl options = new PbcsRetrieveOptionsImpl();

        options.setSuppressMissingColumns(true);

        assertThat(options.isSuppressMissingColumns(), is(true));
        assertThat(options.isSuppressMissingRows(), is(false));
    }

    @Test
    public void suppressMissingRowsAndColumnsCanBeEnabledIndependently() {
        PbcsRetrieveOptionsImpl options = new PbcsRetrieveOptionsImpl();

        options.setSuppressMissing(true);
        options.setSuppressMissingColumns(true);

        assertThat(options.isSuppressMissingRows(), is(true));
        assertThat(options.isSuppressMissingColumns(), is(true));
    }

}
