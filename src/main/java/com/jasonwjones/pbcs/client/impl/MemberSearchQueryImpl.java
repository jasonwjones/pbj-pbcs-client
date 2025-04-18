package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.client.MemberSearchQuery;

import java.util.StringJoiner;

public class MemberSearchQueryImpl implements MemberSearchQuery {

    private Type type;

    private String memberName;

    private String dimensionName;

    private String searchTerm;

    private boolean caseSensitive;

    private boolean searchAliases;

    @Override
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
    @Override
    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    @Override
    public String getDimensionName() {
        return dimensionName;
    }

    public void setDimensionName(String dimensionName) {
        this.dimensionName = dimensionName;
    }

    @Override
    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    @Override
    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    @Override
    public boolean isSearchAliases() {
        return searchAliases;
    }

    public void setSearchAliases(boolean searchAliases) {
        this.searchAliases = searchAliases;
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
                .toString();
    }

}