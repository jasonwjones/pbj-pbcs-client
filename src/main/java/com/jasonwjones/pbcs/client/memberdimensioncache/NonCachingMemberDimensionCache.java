package com.jasonwjones.pbcs.client.memberdimensioncache;

import com.jasonwjones.pbcs.client.PbcsPlanType;

/**
 * A simple implementation that will never store any dimension information. In other words, the {@link PbcsPlanType#getMember(String)}
 * method will always end up brute-forcing the dimension for the given member. This implementation is ostensibly provided
 * if you just really don't want any caching to ever happen.
 */
public class NonCachingMemberDimensionCache implements PbcsPlanType.MemberDimensionCache {

    private static final NonCachingMemberDimensionCache INSTANCE = new NonCachingMemberDimensionCache();

    @Override
    public String getDimensionName(String memberName) {
        return null;
    }

    @Override
    public String getDimensionName(PbcsPlanType planType, String memberName) {
        return null;
    }

    @Override
    public void setDimension(String memberName, String dimensionName) {
        // nothing
    }

    @Override
    public void setDimension(PbcsPlanType planType, String memberName, String dimensionName) {
        // nothing
    }

    public static NonCachingMemberDimensionCache getInstance() {
        return INSTANCE;
    }

}