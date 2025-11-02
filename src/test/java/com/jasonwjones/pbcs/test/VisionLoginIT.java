package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsConnection;
import com.jasonwjones.pbcs.client.exceptions.PbcsInvalidCredentialsException;
import com.jasonwjones.pbcs.client.impl.PbcsConnectionImpl;
import com.jasonwjones.pbcs.util.ConnectionUtils;
import org.junit.Test;

import static org.junit.Assert.assertThrows;

public class VisionLoginIT {

    @Test
    public void whenLoginWithBadCredentials() {
        PbcsConnection defaultConnection = ConnectionUtils.defaultConnection();
        PbcsConnection connection = new PbcsConnectionImpl(defaultConnection.getServer(), defaultConnection.getIdentityDomain(), "invalid_user@example.com", "invalid_password");
        PbcsClientFactory clientFactory = new PbcsClientFactory();
        assertThrows(PbcsInvalidCredentialsException.class, () -> clientFactory.createClient(connection));
    }

}