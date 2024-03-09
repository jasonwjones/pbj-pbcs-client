package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.api.v3.PbcsMemberPropertiesImpl;
import com.jasonwjones.pbcs.client.*;

import java.util.ArrayList;
import java.util.List;

public class PbcsMemberImpl implements PbcsMember {

    private final PbcsApplication application;

    private final PbcsMemberPropertiesImpl memberProperties;

    public PbcsMemberImpl(PbcsApplication application, PbcsMemberPropertiesImpl memberProperties) {
        this.application = application;
        this.memberProperties = memberProperties;
    }

    @Override
    public PbcsMemberType getType() {
        return PbcsMemberType.valueOf(getObjectNumericType());
    }

    @Override
    public String getAlias() {
        return memberProperties.getAlias();
    }

    @Override
    public List<PbcsMember> getChildren() {
        List<PbcsMember> children = new ArrayList<>();
        for (PbcsMemberPropertiesImpl child : memberProperties.getChildren()) {
            children.add(new PbcsMemberImpl(application, child));
        }
        return children;
    }

    @Override
    public String getDescription() {
        return memberProperties.getDescription();
    }

    @Override
    public String getParentName() {
        return memberProperties.getParentName();
    }

    @Override
    public String getDataType() {
        return memberProperties.getDataType();
    }

    @Override
    public Integer getObjectNumericType() {
        return memberProperties.getObjectType();
    }

    @Override
    public String getDataStorage() {
        return memberProperties.getDataStorage();
    }

    @Override
    public DataStorage getDataStorageType() {
        return DataStorage.valueOfOrOther(memberProperties.getDataStorage());
    }


    @Override
    public String getDimensionName() {
        return memberProperties.getDimensionName();
    }

    @Override
    public boolean isTwoPass() {
        return memberProperties.isTwoPass();
    }

    @Override
    public List<String> getUsedIn() {
        return memberProperties.getUsedIn();
    }

    @Override
    public int getLevel() {
        if (getChildren() == null || getChildren().isEmpty()) {
            return 0;
        } else {
            int minLevel = -1;
            for (PbcsMemberProperties child : getChildren()) {
                if (minLevel == -1) {
                    minLevel = child.getLevel();
                } else {
                    minLevel = Math.min(minLevel, child.getLevel());
                }
            }
            return minLevel + 1;
        }
    }


    @Override
    public int getGeneration() {
        return memberProperties.getGeneration();
    }

    @Override
    public String getName() {
        return memberProperties.getName();
    }

    @Override
    public PbcsObjectType getObjectType() {
        return PbcsObjectType.MEMBER;
    }

    @Override
    public boolean isLeaf() {
        return getChildren().isEmpty();
    }


    @Override
    public String toString() {
        String aliasText = getAlias() != null ? " (alias: " + getAlias() + ")" : "";
        return getName() + aliasText;
    }

    @Override
    public PbcsApplication getApplication() {
        return application;
    }

}