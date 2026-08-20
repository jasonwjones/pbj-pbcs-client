package com.jasonwjones.pbcs.client.sso;

/**
 * Persists refresh tokens for the IDCS device code and JWT authentication flows, keyed by tenant, client ID,
 * and scope.
 */
public interface RefreshTokenStorage {

    /**
     * Stores the refresh token for the given tenant, client ID, and scope.
     *
     * @param tenant the tenant
     * @param clientId the client ID
     * @param scope the scope
     * @param deviceCode the refresh token to store
     */
    void put(String tenant, String clientId, String scope, String deviceCode);

    /**
     * Gets the stored refresh token for the given tenant, client ID, and scope.
     *
     * @param tenant the tenant
     * @param clientId the client ID
     * @param scope the scope
     * @return the stored refresh token, or null if none is stored
     */
    String getRefreshToken(String tenant, String clientId, String scope);

    /**
     * Clears the stored refresh token for the given tenant, client ID, and scope.
     *
     * @param tenant the tenant
     * @param clientId the client ID
     * @param scope the scope
     * @return true if a stored token was cleared, false otherwise
     */
    boolean clear(String tenant, String clientId, String scope);

    /**
     * Clears all stored refresh tokens.
     */
    void clear();

}
