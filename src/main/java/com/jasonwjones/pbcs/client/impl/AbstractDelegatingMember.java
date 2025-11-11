package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.client.*;

import java.util.List;
import java.util.Objects;

public abstract class AbstractDelegatingMember implements PbcsMember {

    private final PbcsPlanType planType;

    private final String memberName;

    private final String dimensionName;

    private PbcsMember member;

    protected AbstractDelegatingMember(PbcsPlanType planType, String memberName, String dimensionName) {
        this.planType = Objects.requireNonNull(planType, "Plan type must not be null");
        this.memberName = Objects.requireNonNull(memberName, "Member name must not be null");
        this.dimensionName = Objects.requireNonNull(dimensionName, "Dimension name must not be null");
    }

    protected PbcsMember member() {
        if (member == null) {
            member = planType.getMember(getDimensionName(), getName());
        }
        return member;
    }

    @Override
    public String getName() {
        return memberName;
    }

    @Override
    public PbcsObjectType getObjectType() {
        return PbcsObjectType.MEMBER;
    }

    @Override
    public String getDimensionName() {
        return dimensionName;
    }

    @Override
    public String getAlias() {
        return member().getAlias();
    }

    @Override
    public String getOldName() {
        return member().getOldName();
    }

    @Override
    public List<PbcsMember> getChildren() {
        return member().getChildren();
    }

    @Override
    public boolean isLeaf() {
        return member().isLeaf();
    }

    @Override
    public String getDescription() {
        return member().getDescription();
    }

    @Override
    public String getParentName() {
        return member().getParentName();
    }

    @Override
    public PbcsApplication getParent() {
        return member().getParent();
    }

    @Override
    public PbcsMember getParentMember() {
        return member().getParentMember();
    }

    @Override
    public String getDataType() {
        return member().getDataType();
    }

    @Override
    public Integer getObjectNumericType() {
        return member().getObjectNumericType();
    }

    @Override
    public PbcsMemberType getType() {
        return member().getType();
    }

    @Override
    public String getDataStorage() {
        return member().getDataStorage();
    }

    @Override
    public DataStorage getDataStorageType() {
        return member().getDataStorageType();
    }

    @Override
    public boolean isTwoPass() {
        return member().isTwoPass();
    }

    @Override
    public List<String> getUsedIn() {
        return member().getUsedIn();
    }

    @Override
    public int getLevel() {
        return member().getLevel();
    }

    @Override
    public int getGeneration() {
        return member().getGeneration();
    }

    @Override
    public PbcsMember searchForDescendant(String memberOrAliasName) {
        return member().searchForDescendant(memberOrAliasName);
    }

    @Override
    public int getMaxGeneration() {
        return member().getMaxGeneration();
    }

    @Override
    public PbcsApplication getApplication() {
        return member().getApplication();
    }

}