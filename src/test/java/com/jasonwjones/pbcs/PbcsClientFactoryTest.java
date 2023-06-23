package com.jasonwjones.pbcs;

import com.jasonwjones.pbcs.client.PbcsConnection;
import com.jasonwjones.pbcs.client.impl.PbcsConnectionImpl;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PbcsClientFactoryTest {

    private PbcsClientFactory clientFactory;

    @Before
    public void setUp() {
        clientFactory = new PbcsClientFactory();
    }

    @Test
    public void createClient() {
        PbcsConnection connection = new PbcsConnectionImpl("server", "", "jason", "password");
        PbcsClient client = clientFactory.createClient(connection);
        assertNotNull(client);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createClientWithBadServerName() {
        PbcsConnection connection = new PbcsConnectionImpl("https://server/foo", "", "jason", "password");
        clientFactory.createClient(connection);
    }

}