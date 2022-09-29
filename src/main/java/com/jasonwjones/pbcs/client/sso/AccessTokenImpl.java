package com.jasonwjones.pbcs.client.sso;

public class AccessTokenImpl implements AccessToken {

    private final String accessToken;

    public AccessTokenImpl(String accessToken) {
        if (accessToken == null) throw new IllegalArgumentException("Must specify token value");
        this.accessToken = accessToken;
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }

}