package com.jasonwjones.pbcs.client.sso;

public interface RefreshTokenStorage {

    void put(String tenant, String clientId, String scope, String deviceCode);

    String getRefreshToken(String tenant, String clientId, String scope);

    boolean clear(String tenant, String clientId, String scope);

    void clear();

}