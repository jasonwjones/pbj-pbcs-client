package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.client.memberdimensioncache.InMemoryMemberDimensionCache;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class PlanTypeConfigurationImpl implements PbcsApplication.PlanTypeConfiguration {

    private String name;

    private boolean skipCheck;

    private boolean validateDimensions;

    private List<String> explicitDimensions;

    private List<String> explicitAttributeDimensions;

    private PbcsPlanType.MemberDimensionCache memberDimensionCache = new InMemoryMemberDimensionCache();

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isSkipCheck() {
        return skipCheck;
    }

    public void setValidateDimensions(boolean validateDimensions) {
        this.validateDimensions = validateDimensions;
    }

    @Override
    public boolean isValidateDimensions() {
        return validateDimensions;
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

    @Override
    public List<String> getExplicitAttributeDimensions() {
        return explicitAttributeDimensions;
    }

    public void setExplicitAttributeDimensions(List<String> explicitAttributeDimensions) {
        this.explicitAttributeDimensions = explicitAttributeDimensions;
    }

    public void setMemberDimensionCache(PbcsPlanType.MemberDimensionCache memberDimensionCache) {
        this.memberDimensionCache = memberDimensionCache;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PlanTypeConfigurationImpl.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("skipCheck=" + skipCheck)
                .add("validateDimensions=" + validateDimensions)
                .add("explicitDimensions=" + explicitDimensions)
                .add("explicitAttributeDimensions=" + explicitAttributeDimensions)
                .add("memberDimensionCache=" + memberDimensionCache.getClass().getSimpleName())
                .toString();
    }

    public static class Builder {

        private final PlanTypeConfigurationImpl configuration;

        public Builder(String name) {
            configuration = new PlanTypeConfigurationImpl();
            configuration.setName(name);
        }

        public Builder skipCheck() {
            configuration.setSkipCheck(true);
            return this;
        }

        public Builder dimension(String dimension) {
            if (configuration.getExplicitDimensions() == null) configuration.setExplicitDimensions(new ArrayList<>());
            configuration.getExplicitDimensions().add(dimension);
            return this;
        }

        public Builder dimensions(List<String> dimensions) {
            for (String dimension : dimensions) {
                dimension(dimension);
            }
            return this;
        }

        public Builder validateDimensions() {
            configuration.setValidateDimensions(true);
            return this;
        }

        public PbcsApplication.PlanTypeConfiguration build() {
            return configuration;
        }

    }

}