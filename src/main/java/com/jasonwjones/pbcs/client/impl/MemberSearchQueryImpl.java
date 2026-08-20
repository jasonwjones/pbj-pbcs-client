package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.client.MemberSearchQuery;

import java.util.StringJoiner;

/**
 * Default, mutable {@link MemberSearchQuery} implementation.
 */
public class MemberSearchQueryImpl implements MemberSearchQuery {

    private Type type;

    private String memberName;

    private String dimensionName;

    private String searchTerm;

    private boolean caseSensitive;

    private boolean searchAliases;

    private boolean stopWhenFound;

    private boolean excludeShares;

    /**
     * Constructs an empty instance.
     */
    public MemberSearchQueryImpl() {
    }

    @Override
    public Type getType() {
        return type;
    }

    /**
     * Sets the type of query to perform.
     *
     * @param type the query type
     */
    public void setType(Type type) {
        this.type = type;
    }
    @Override
    public String getMemberName() {
        return memberName;
    }

    /**
     * Sets the base member name to search from.
     *
     * @param memberName the member name
     */
    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    @Override
    public String getDimensionName() {
        return dimensionName;
    }

    /**
     * Sets the dimension to search within.
     *
     * @param dimensionName the dimension name
     */
    public void setDimensionName(String dimensionName) {
        this.dimensionName = dimensionName;
    }

    @Override
    public String getSearchTerm() {
        return searchTerm;
    }

    /**
     * Sets the search term to match against.
     *
     * @param searchTerm the search term
     */
    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    @Override
    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    /**
     * Sets whether the search should be case-sensitive.
     *
     * @param caseSensitive true for case-sensitive matching, false otherwise
     */
    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    @Override
    public boolean isSearchAliases() {
        return searchAliases;
    }

    /**
     * Sets whether aliases should also be considered when matching.
     *
     * @param searchAliases true to include aliases, false otherwise
     */
    public void setSearchAliases(boolean searchAliases) {
        this.searchAliases = searchAliases;
    }

    @Override
    public boolean isStopWhenFound() {
        return stopWhenFound;
    }

    /**
     * Sets whether the search should stop as soon as a match is found.
     *
     * @param stopWhenFound true to stop on first match, false otherwise
     */
    public void setStopWhenFound(boolean stopWhenFound) {
        this.stopWhenFound = stopWhenFound;
    }

    @Override
    public boolean isExcludeShares() {
        return excludeShares;
    }

    /**
     * Sets whether shared members should be excluded from the results.
     *
     * @param excludeShares true to exclude shared members, false otherwise
     */
    public void setExcludeShares(boolean excludeShares) {
        this.excludeShares = excludeShares;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MemberSearchQueryImpl.class.getSimpleName() + "[", "]")
                .add("type=" + type)
                .add("memberName='" + memberName + "'")
                .add("dimensionName='" + dimensionName + "'")
                .add("searchTerm='" + searchTerm + "'")
                .add("caseSensitive=" + caseSensitive)
                .add("searchAliases=" + searchAliases)
                .add("stopWhenFound=" + stopWhenFound)
                .add("excludeShares=" + excludeShares)
                .toString();
    }

}
