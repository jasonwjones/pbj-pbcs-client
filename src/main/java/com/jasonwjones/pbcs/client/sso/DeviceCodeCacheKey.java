package com.jasonwjones.pbcs.client.sso;

import java.util.Objects;

/**
 * Key used to cache device codes (or their resulting tokens) by tenant, client ID, and scope.
 */
public class DeviceCodeCacheKey {

    private final String tenant;

    private final String clientId;

    private final String scope;

    /**
     * Constructs an instance for the given tenant, client ID, and scope.
     *
     * @param tenant the tenant
     * @param clientId the client ID
     * @param scope the scope
     */
    public DeviceCodeCacheKey(String tenant, String clientId, String scope) {
        this.tenant = tenant;
        this.clientId = clientId;
        this.scope = scope;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceCodeCacheKey that = (DeviceCodeCacheKey) o;
        return tenant.equals(that.tenant) && clientId.equals(that.clientId) && scope.equals(that.scope);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenant, clientId, scope);
    }

}
