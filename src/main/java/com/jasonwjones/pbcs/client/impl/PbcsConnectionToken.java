package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.client.PbcsConnection;
import com.jasonwjones.pbcs.client.sso.RefreshableToken;

public class PbcsConnectionToken implements PbcsConnection {

    private final String server;

    private final RefreshableToken refreshableToken;

    public PbcsConnectionToken(String server, RefreshableToken refreshableToken) {
        this.server = server;
        this.refreshableToken = refreshableToken;
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
        return refreshableToken.getAccessToken();
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