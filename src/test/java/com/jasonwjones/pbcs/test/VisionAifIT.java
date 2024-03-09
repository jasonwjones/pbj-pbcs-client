package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.client.PbcsAppDimension;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsDimension;
import com.jasonwjones.pbcs.utils.PbcsClientUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class VisionAifIT {

    protected PbcsApplication app;

    @Before
    public void setUp() {
        app = PbcsClientUtils.vision();
    }

    @Test
    public void getDimensions() {
       List<PbcsAppDimension> dimensions = app.getDimensions();
       for (PbcsAppDimension dimension : dimensions) {
           System.out.println("Dim: " + dimension.getName());
       }
    }

}