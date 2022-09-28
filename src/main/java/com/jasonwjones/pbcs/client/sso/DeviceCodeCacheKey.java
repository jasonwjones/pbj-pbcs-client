package com.jasonwjones.pbcs.client.sso;

import java.util.Objects;

public class DeviceCodeCacheKey {

    private final String tenant;

    private final String clientId;

    private final String scope;

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