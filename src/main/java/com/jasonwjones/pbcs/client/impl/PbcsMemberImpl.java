package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.api.v3.PbcsMemberPropertiesImpl;
import com.jasonwjones.pbcs.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class PbcsMemberImpl extends AbstractPbcsObject implements PbcsMember {

    private static final Logger logger = LoggerFactory.getLogger(PbcsMemberImpl.class);

    private final PbcsApplication application;

    private final PbcsMemberPropertiesImpl memberProperties;

    public PbcsMemberImpl(RestContext context, PbcsApplication application, PbcsMemberPropertiesImpl memberProperties) {
        super(context);
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
    public String getOldName() {
        return memberProperties.getOldName();
    }

    @Override
    public List<PbcsMember> getChildren() {
        List<PbcsMember> children = new ArrayList<>();
        for (PbcsMemberPropertiesImpl child : memberProperties.getChildren()) {
            children.add(new PbcsMemberImpl(context, application, child));
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
    public PbcsApplication getParent() {
        return getApplication();
    }

    @Override
    public PbcsApplication getApplication() {
        return application;
    }

    @Override
    public PbcsMember getParentMember() {
        if (getParentName() != null) {
            logger.info("Resolving parent of {}", getName());
            // potentially resolving multiple times if you keep calling this
            return application.getMember(getDimensionName(), getParentName());
        } else {
            return null;
        }
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
            for (PbcsMember child : getChildren()) {
                if (minLevel == -1) {
                    minLevel = child.getLevel();
                } else {
                    minLevel = Math.max(minLevel, child.getLevel());
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
    public PbcsMember searchForDescendant(String memberOrAliasName) {
        Queue<PbcsMember> members = new ArrayDeque<>();
        members.add(this);

        while (!members.isEmpty()) {
            PbcsMember current = members.remove();
            if (memberOrAliasName.equalsIgnoreCase(current.getName()) || memberOrAliasName.equalsIgnoreCase(current.getAlias())) {
                // this is technically unneeded if the dimension is the same as possibleDimension, but it will be
                // set here anyway in case the underlying cache mechanism needs a "hit" in order to update a TTL
                // or similar value. Note: this could cause a lot of traffic to your SoR if the cache writes through
                // You may want to use a putIfAbsent paradigm (instead of a put) to avoid unnecessary writes
                if (current.getDataStorageType() == DataStorage.SHARED) {
                    logger.info("Found descendant {} of {} but skipping since it's a share", memberOrAliasName, getName());
                } else {
                    return current;
                }
            }
            members.addAll(current.getChildren());
        }
        return null;
    }

}