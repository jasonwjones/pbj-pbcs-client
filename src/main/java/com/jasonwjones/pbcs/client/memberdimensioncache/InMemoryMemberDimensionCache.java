package com.jasonwjones.pbcs.client.memberdimensioncache;

import com.jasonwjones.pbcs.client.PbcsPlanType;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A simple implementation that stores all the known member/dimension entries in a {@link ConcurrentMap}.
 */
public class InMemoryMemberDimensionCache implements PbcsPlanType.MemberDimensionCache {

    private final ConcurrentMap<String, String> dimensionLookup = new ConcurrentHashMap<>();

    @Override
    public String getDimensionName(PbcsPlanType planType, String memberName) {
        return dimensionLookup.get(memberName);
    }

    @Override
    public void setDimension(PbcsPlanType planType, String memberName, String dimensionName) {
        dimensionLookup.put(memberName, dimensionName);
    }

}