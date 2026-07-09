package com.jasonwjones.pbcs.client.memberdimensioncache;

import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsMember;
import com.jasonwjones.pbcs.util.PbcsClientUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class PropertiesMemberDimensionCacheIT {

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