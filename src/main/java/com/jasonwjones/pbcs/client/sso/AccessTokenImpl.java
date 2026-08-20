package com.jasonwjones.pbcs.client.sso;

/**
 * Simple, immutable {@link AccessToken} implementation that wraps a raw token value.
 */
public class AccessTokenImpl implements AccessToken {

    private final String accessToken;

    /**
     * Constructs an instance wrapping the given token value.
     *
     * @param accessToken the access token value, must not be null
     * @throws IllegalArgumentException if accessToken is null
     */
    public AccessTokenImpl(String accessToken) {
        if (accessToken == null) throw new IllegalArgumentException("Must specify token value");
        this.accessToken = accessToken;
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }

}
