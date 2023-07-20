package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.client.memberdimensioncache.InMemoryMemberDimensionCache;

import java.util.List;
import java.util.StringJoiner;

public class PlanTypeConfigurationImpl implements PbcsApplication.PlanTypeConfiguration {

    private String name;

    private boolean skipCheck;

    private List<String> explicitDimensions;

    private PbcsPlanType.MemberDimensionCache memberDimensionCache = new InMemoryMemberDimensionCache();

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isSkipCheck() {
        return skipCheck;
    }

    @Override
    public List<String> getExplicitDimensions() {
        return explicitDimensions;
    }

    @Override
    public PbcsPlanType.MemberDimensionCache getMemberDimensionCache() {
        return memberDimensionCache;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSkipCheck(boolean skipCheck) {
        this.skipCheck = skipCheck;
    }

    public void setExplicitDimensions(List<String> explicitDimensions) {
        this.explicitDimensions = explicitDimensions;
    }

    public void setMemberDimensionCache(PbcsPlanType.MemberDimensionCache memberDimensionCache) {
        this.memberDimensionCache = memberDimensionCache;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PlanTypeConfigurationImpl.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("skipCheck=" + skipCheck)
                .add("explicitDimensions=" + explicitDimensions)
                .add("memberDimensionCache=" + memberDimensionCache.getClass().getSimpleName())
                .toString();
    }

}