package com.jasonwjones.pbcs.client.sso.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Maps the token response returned by IDCS/OCI IAM (Oracle's identity service) token endpoints, such as
 * those used by the device code and JWT-assertion flows.
 */
public class AccessTokenResponse {

    // The access token, which can be used to sign on to the PBCS REST API
    @JsonProperty("access_token")
    private String accessToken;

    // Bearer
    @JsonProperty("token_type")
    private String tokenType;

    // when the access token expires
    @JsonProperty("expires_in")
    private Integer expiresIn;

    @JsonProperty("refresh_token")
    private String refreshToken;

    /**
     * Constructs an empty instance for deserialization.
     */
    public AccessTokenResponse() {
    }

    /**
     * Gets the access token, which can be used to sign on to the PBCS REST API.
     *
     * @return the access token
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Sets the access token.
     *
     * @param accessToken the access token
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * Gets the token type, typically {@code Bearer}.
     *
     * @return the token type
     */
    public String getTokenType() {
        return tokenType;
    }

    /**
     * Sets the token type.
     *
     * @param tokenType the token type
     */
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    /**
     * Gets the number of seconds until the access token expires.
     *
     * @return the expiration time, in seconds from issuance
     */
    public Integer getExpiresIn() {
        return expiresIn;
    }

    /**
     * Sets the number of seconds until the access token expires.
     *
     * @param expiresIn the expiration time, in seconds from issuance
     */
    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

    /**
     * Gets the refresh token, if one was issued, which can be used to obtain a new access token without
     * requiring the user to re-authenticate.
     *
     * @return the refresh token, may be null if none was issued
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * Sets the refresh token.
     *
     * @param refreshToken the refresh token
     */
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}
