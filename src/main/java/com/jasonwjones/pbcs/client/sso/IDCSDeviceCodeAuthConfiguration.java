package com.jasonwjones.pbcs.client.sso;

/**
 * Holds the configuration needed to run the IDCS OAuth2 device code flow: the client ID, tenant, scope,
 * refresh token storage, and the derived token/device endpoints.
 */
public class IDCSDeviceCodeAuthConfiguration {

    private String clientId;

    private String tenant;

    private String scope;

    private RefreshTokenStorage refreshTokenStorage;

    //"https://idcs-" + tenant + ".identity.oraclecloud.com/oauth2/v1/token"
    private String tokenEndpoint;

    private String deviceEndpoint;

    /**
     * Constructs an empty instance.
     */
    public IDCSDeviceCodeAuthConfiguration() {
    }

}
