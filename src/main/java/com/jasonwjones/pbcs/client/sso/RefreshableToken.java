package com.jasonwjones.pbcs.client.sso;

public interface RefreshableToken extends AccessToken {

    void refresh();

    boolean isExpired();

}