package com.jasonwjones.pbcs.client.memberdimensioncache;

import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsMember;
import com.jasonwjones.pbcs.util.PbcsClientUtils;
import com.jasonwjones.pbcs.testing.LiveEpmTestSupport;
import com.jasonwjones.pbcs.testing.ReadOnlyIntegrationTest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.IOException;

@Category(ReadOnlyIntegrationTest.class)
public class PropertiesMemberDimensionCacheIT {

    @BeforeClass
    public static void requireLiveEpmCredentials() {
        LiveEpmTestSupport.assumeDefaultConnectionAvailable();
    }

    private File file;

    private PropertiesMemberDimensionCache cache;

    private PbcsApplication application;

    @Before
    public void setUp() throws Exception {
        file = File.createTempFile(this.getClass().getName(), ".tmp");
        cache = new PropertiesMemberDimensionCache(file);
        application = PbcsClientUtils.vision();
    }

    @Test
    public void setMember() {
        PbcsMember member = application.getMember("Period", "Jan");
        cache.setMember(null, "Jan", member);
    }

    @Test
    public void clear() throws IOException {
        cache.clear();
    }

}
