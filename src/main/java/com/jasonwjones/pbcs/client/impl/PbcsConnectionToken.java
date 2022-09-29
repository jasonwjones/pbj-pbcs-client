package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.client.PbcsConnection;
import com.jasonwjones.pbcs.client.sso.AccessToken;
import com.jasonwjones.pbcs.client.sso.RefreshableToken;

public class PbcsConnectionToken implements PbcsConnection {

    private final String server;

    private final AccessToken accessToken;

    public PbcsConnectionToken(String server, AccessToken accessToken) {
        this.server = server;
        this.accessToken = accessToken;
    }

    @Override
    public String getServer() {
        return server;
    }

    @Override
    public String getIdentityDomain() {
        return null;
        //throw new UnsupportedOperationException("Cannot get identity domain with token connection");
    }

    @Override
    public String getUsername() {
        return accessToken.getAccessToken();
    }

    @Override
    public String getPassword() {
        return null;
        //throw new UnsupportedOperationException("Cannot get password with token connection");
    }

    @Override
    public boolean isToken() {
        return true;
    }

}