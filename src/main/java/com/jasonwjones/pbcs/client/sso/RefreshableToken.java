package com.jasonwjones.pbcs.client.sso;

/**
 * An {@link AccessToken} that can refresh itself once expired.
 */
public interface RefreshableToken extends AccessToken {

    /**
     * Forces this token to be refreshed.
     */
    void refresh();

    /**
     * Whether this token is currently expired.
     *
     * @return true if expired, false otherwise
     */
    boolean isExpired();

}
