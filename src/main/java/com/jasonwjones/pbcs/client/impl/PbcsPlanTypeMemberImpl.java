package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.api.v3.PbcsMemberPropertiesImpl;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsMember;
import com.jasonwjones.pbcs.client.PbcsPlanType;

import java.util.List;

final class PbcsPlanTypeMemberImpl extends PbcsMemberImpl {

    private final PbcsPlanType planType;

    PbcsPlanTypeMemberImpl(RestContext context, PbcsApplication application, PbcsPlanType planType, PbcsMemberPropertiesImpl memberProperties) {
        super(context, application, memberProperties);
        this.planType = planType;
    }

    @Override
    boolean includeMember(PbcsMemberPropertiesImpl memberProperties) {
        return isAvailableInPlan(memberProperties, planType);
    }

    @Override
    PbcsMember createMember(PbcsMemberPropertiesImpl memberProperties) {
        return new PbcsPlanTypeMemberImpl(context, getApplication(), planType, memberProperties);
    }

    @Override
    public PbcsMember getParentMember() {
        return getParentName() != null ? planType.getMember(getDimensionName(), getParentName()) : null;
    }

    static boolean isAvailableInPlan(PbcsMemberPropertiesImpl memberProperties, PbcsPlanType planType) {
        List<String> usedIn = memberProperties.getUsedIn();
        return usedIn == null || usedIn.isEmpty() || usedIn.contains(planType.getName());
    }

}
