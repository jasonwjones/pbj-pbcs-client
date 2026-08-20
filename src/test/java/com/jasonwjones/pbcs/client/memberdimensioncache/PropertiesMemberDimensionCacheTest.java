package com.jasonwjones.pbcs.client.memberdimensioncache;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class PropertiesMemberDimensionCacheTest {

    private File file;

    private PropertiesMemberDimensionCache cache;

    @Before
    public void setUp() throws IOException {
        file = File.createTempFile(this.getClass().getName(), ".tmp");
        cache = new PropertiesMemberDimensionCache(file);
    }

    @After
    public void tearDown() {
        file.delete();
    }

    @Test
    public void getAliasReturnsNullWhenNothingIsCached() {
        assertThat(cache.getAlias(null, "Actual", "Alias2"), is(nullValue()));
    }

    @Test
    public void setAliasThenGetAliasReturnsTheStoredValue() {
        cache.setAlias(null, "Actual", "Alias2", "Actual Alias");

        assertThat(cache.getAlias(null, "Actual", "Alias2"), is("Actual Alias"));
    }

    @Test
    public void aliasesForDifferentTablesAreStoredIndependently() {
        cache.setAlias(null, "Actual", "Default", "Default Alias");
        cache.setAlias(null, "Actual", "Alias2", "Alias2 Alias");

        assertThat(cache.getAlias(null, "Actual", "Default"), is("Default Alias"));
        assertThat(cache.getAlias(null, "Actual", "Alias2"), is("Alias2 Alias"));
    }

    @Test
    public void nullAndBlankAliasTableNameAreTreatedAsTheDefaultTable() {
        cache.setAlias(null, "Actual", "Default", "Default Alias");

        assertThat(cache.getAlias(null, "Actual", null), is("Default Alias"));
        assertThat(cache.getAlias(null, "Actual", ""), is("Default Alias"));
    }

    @Test
    public void aliasesPersistAcrossCacheInstancesBackedByTheSameFile() {
        cache.setAlias(null, "Actual", "Alias2", "Actual Alias");

        PropertiesMemberDimensionCache reloaded = new PropertiesMemberDimensionCache(file);

        assertThat(reloaded.getAlias(null, "Actual", "Alias2"), is("Actual Alias"));
    }

}
