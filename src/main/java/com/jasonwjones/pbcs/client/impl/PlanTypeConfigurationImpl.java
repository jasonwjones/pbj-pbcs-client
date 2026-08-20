package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.client.memberdimensioncache.InMemoryMemberDimensionCache;
import com.jasonwjones.pbcs.client.memberdimensioncache.NonCachingMemberDimensionCache;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Default, mutable {@link PbcsApplication.PlanTypeConfiguration} implementation, and a fluent
 * {@link Builder} for constructing one.
 */
public class PlanTypeConfigurationImpl implements PbcsApplication.PlanTypeConfiguration {

    /**
     * Constructs an empty instance.
     */
    public PlanTypeConfigurationImpl() {
    }

    private String name;

    private boolean skipCheck;

    private boolean queryDimensions;

    private boolean validateDimensions;

    private List<String> explicitDimensions;

    private List<String> explicitAttributeDimensions;

    private PbcsPlanType.MemberDimensionCache memberDimensionCache = new InMemoryMemberDimensionCache();

    private PbcsPlanType.MemberResolver memberResolver = NonCachingMemberDimensionCache.getInstance();

    private int memberSearchThreads = 1;

    private boolean ignoreAliases;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isSkipCheck() {
        return skipCheck;
    }

    @Override
    public boolean isQueryDimensions() {
        return queryDimensions;
    }

    /**
     * Sets whether dimensions should be queried from the DM/AIF endpoint rather than using explicit dimensions.
     *
     * @param queryDimensions true to query dimensions, false otherwise
     */
    public void setQueryDimensions(boolean queryDimensions) {
        this.queryDimensions = queryDimensions;
    }

    /**
     * Sets whether the explicit dimensions should be validated against the plan type.
     *
     * @param validateDimensions true to validate, false otherwise
     */
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

    /**
     * Sets the plan type name.
     *
     * @param name the plan type name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets whether the plan type existence check should be skipped.
     *
     * @param skipCheck true to skip the check, false otherwise
     */
    public void setSkipCheck(boolean skipCheck) {
        this.skipCheck = skipCheck;
    }

    /**
     * Sets the explicit dimensions for this plan type.
     *
     * @param explicitDimensions the explicit dimensions
     */
    public void setExplicitDimensions(List<String> explicitDimensions) {
        this.explicitDimensions = explicitDimensions;
    }

    @Override
    public List<String> getExplicitAttributeDimensions() {
        return explicitAttributeDimensions;
    }

    /**
     * Sets the explicit attribute dimensions for this plan type.
     *
     * @param explicitAttributeDimensions the explicit attribute dimensions
     */
    public void setExplicitAttributeDimensions(List<String> explicitAttributeDimensions) {
        this.explicitAttributeDimensions = explicitAttributeDimensions;
    }

    /**
     * Sets the member dimension cache to use.
     *
     * @param memberDimensionCache the member dimension cache
     */
    public void setMemberDimensionCache(PbcsPlanType.MemberDimensionCache memberDimensionCache) {
        this.memberDimensionCache = memberDimensionCache;
    }

    @Override
    public PbcsPlanType.MemberResolver getMemberResolver() {
        return memberResolver;
    }

    /**
     * Sets the member resolver to use.
     *
     * @param memberResolver the member resolver
     */
    public void setMemberResolver(PbcsPlanType.MemberResolver memberResolver) {
        this.memberResolver = memberResolver;
    }

    @Override
    public int getMemberSearchThreads() {
        return memberSearchThreads;
    }

    /**
     * Sets the number of threads to use for brute-force member searches.
     *
     * @param memberSearchThreads the number of threads
     */
    public void setMemberSearchThreads(int memberSearchThreads) {
        this.memberSearchThreads = memberSearchThreads;
    }

    @Override
    public boolean isIgnoreAliases() {
        return ignoreAliases;
    }

    /**
     * Sets whether aliases should be ignored when resolving members.
     *
     * @param ignoreAliases true to ignore aliases, false otherwise
     */
    public void setIgnoreAliases(boolean ignoreAliases) {
        this.ignoreAliases = ignoreAliases;
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
                .add("memberResolver=" + (memberResolver != null ? memberResolver.getClass().getSimpleName() : "null"))
                .add("memberSearchThreads=" + memberSearchThreads)
                .add("ignoreAliases=" + ignoreAliases)
                .toString();
    }

    /**
     * A fluent builder for constructing a {@link PbcsApplication.PlanTypeConfiguration}.
     */
    public static class Builder {

        private final PlanTypeConfigurationImpl configuration;

        /**
         * Constructs a builder for a plan type configuration with the given name.
         *
         * @param name the plan type name
         */
        public Builder(String name) {
            configuration = new PlanTypeConfigurationImpl();
            configuration.setName(name);
        }

        /**
         * Skips the plan type existence check.
         *
         * @return the builder
         */
        public Builder skipCheck() {
            configuration.setSkipCheck(true);
            return this;
        }

        /**
         * Enables querying dimensions from the DM/AIF endpoint rather than using explicit dimensions.
         *
         * @return the builder
         */
        public Builder queryDimensions() {
            configuration.setQueryDimensions(true);
            return this;
        }

        /**
         * Adds an explicit dimension.
         *
         * @param dimension the dimension name to add
         * @return the builder
         */
        public Builder dimension(String dimension) {
            if (configuration.getExplicitDimensions() == null) configuration.setExplicitDimensions(new ArrayList<>());
            configuration.getExplicitDimensions().add(dimension);
            return this;
        }

        /**
         * Adds multiple explicit dimensions.
         *
         * @param dimensions the dimension names to add
         * @return the builder
         */
        public Builder dimensions(List<String> dimensions) {
            for (String dimension : dimensions) {
                dimension(dimension);
            }
            return this;
        }

        /**
         * Enables validating the explicit dimensions against the plan type.
         *
         * @return the builder
         */
        public Builder validateDimensions() {
            configuration.setValidateDimensions(true);
            return this;
        }

        /**
         * Sets the number of threads to use for brute-force member searches.
         *
         * @param searchThreads the number of threads
         * @return the builder
         */
        public Builder searchThreads(int searchThreads) {
            configuration.setMemberSearchThreads(searchThreads);
            return this;
        }

        /**
         * Sets the member resolver to use.
         *
         * @param memberResolver the member resolver
         * @return the builder
         */
        public Builder memberResolver(PbcsPlanType.MemberResolver memberResolver) {
            configuration.setMemberResolver(memberResolver);
            return this;
        }

        /**
         * Enables ignoring aliases when resolving members.
         *
         * @return the builder
         */
        public Builder ignoreAliases() {
            configuration.setIgnoreAliases(true);
            return this;
        }

        /**
         * Builds the plan type configuration.
         *
         * @return the configured plan type configuration
         */
        public PbcsApplication.PlanTypeConfiguration build() {
            return configuration;
        }

    }

}
