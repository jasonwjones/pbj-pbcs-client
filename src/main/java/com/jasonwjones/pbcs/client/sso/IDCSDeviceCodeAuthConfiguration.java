package com.jasonwjones.pbcs.client.sso;

public class IDCSDeviceCodeAuthConfiguration {

    private String clientId;

    private String tenant;

    private String scope;

    private RefreshTokenStorage refreshTokenStorage;

    //"https://idcs-" + tenant + ".identity.oraclecloud.com/oauth2/v1/token"
    private String tokenEndpoint;

    private String deviceEndpoint;

}