package com.jasonwjones.pbcs.client.sso;

/**
 * Represents an access token that can be used to authenticate calls to the PBCS REST API.
 */
public interface AccessToken {

    /**
     * Gets the raw access token value.
     *
     * @return the access token value
     */
    String getAccessToken();

}
