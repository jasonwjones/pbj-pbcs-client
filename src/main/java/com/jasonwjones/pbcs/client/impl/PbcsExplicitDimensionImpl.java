package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.client.PbcsDimension;
import com.jasonwjones.pbcs.client.PbcsMemberProperties;
import com.jasonwjones.pbcs.client.PbcsPlanType;

import java.util.Collections;
import java.util.Set;

public class PbcsExplicitDimensionImpl implements PbcsDimension {

    private final PbcsPlanType planType;

    private final String name;

    public PbcsExplicitDimensionImpl(PbcsPlanType planType, String name) {
        this.planType = planType;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

//    @Override
//    public Set<String> getValidPlans() {
//        return Collections.singleton(planType.getName());
//    }
//
//    @Override
//    public boolean isValidForPlan(String plan) {
//        return name.equals(plan);
//    }

    @Override
    public PbcsMemberProperties getMember(String memberName) {
        return planType.getMember(name, memberName);
    }

}