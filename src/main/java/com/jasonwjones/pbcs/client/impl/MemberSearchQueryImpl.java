package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.client.MemberSearchQuery;

import java.util.StringJoiner;

public class MemberSearchQueryImpl implements MemberSearchQuery {

    private Type type;

    private String memberName;

    private String dimensionName;

    private String searchTerm;

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
    public String toString() {
        return new StringJoiner(", ", MemberSearchQueryImpl.class.getSimpleName() + "[", "]")
                .add("type=" + type)
                .add("memberName='" + memberName + "'")
                .add("dimensionName='" + dimensionName + "'")
                .add("searchTerm='" + searchTerm + "'")
                .toString();
    }

}