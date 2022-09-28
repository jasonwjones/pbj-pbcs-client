package com.jasonwjones.pbcs.client.sso;

public interface RefreshableToken {

    String getAccessToken();

    void refresh();

    boolean isExpired();

}