package com.jasonwjones.pbcs.client.sso.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

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

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}