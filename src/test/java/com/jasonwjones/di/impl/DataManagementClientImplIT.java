package com.jasonwjones.di.impl;

import com.jasonwjones.di.DataManagementClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.aif.AifDimension;
import com.jasonwjones.pbcs.client.PbcsApi;
import com.jasonwjones.pbcs.client.PbcsConnection;
import com.jasonwjones.pbcs.util.ConnectionUtils;
import com.jasonwjones.pbcs.testing.ReadOnlyIntegrationTest;
import com.jasonwjones.pbcs.testing.LiveEpmTestSupport;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@Category(ReadOnlyIntegrationTest.class)
public class DataManagementClientImplIT {

    @BeforeClass
    public static void requireLiveEpmCredentials() {
        LiveEpmTestSupport.assumeDefaultConnectionAvailable();
    }

    private DataManagementClient client;

    @Before
    public void setUp() {
        PbcsConnection connection = ConnectionUtils.defaultConnection();
        PbcsClientFactory clientFactory = new PbcsClientFactory();
        client = clientFactory.createDataManagementClient(connection);
    }

    @Test
    public void whenGetVersion() {
        PbcsApi api = client.getVersion();
        assertThat(api.getVersion(), is("V1"));
    }

    @Test
    public void whenGetDimensions() {
        List<AifDimension> dimensions = client.getDimensions("Vision");
        assertThat(dimensions, hasSize(8));
    }

}
