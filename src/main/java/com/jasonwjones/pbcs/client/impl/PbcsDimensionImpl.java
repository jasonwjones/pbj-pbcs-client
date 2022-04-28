package com.jasonwjones.pbcs.client.impl;

import java.util.Set;

import com.jasonwjones.pbcs.aif.AifDimension;
import com.jasonwjones.pbcs.client.*;

public class PbcsDimensionImpl implements PbcsAppDimension {

	private final Set<String> plans;

	private final PbcsApplication application;

	private final AifDimension dimension;

	PbcsDimensionImpl(Set<String> plans, PbcsApplication application, AifDimension dimension) {
		this.plans = plans;
		this.application = application;
		this.dimension = dimension;
	}

	@Override
	public String getName() {
		return dimension.getName();
	}

	@Override
	public PbcsMemberProperties getMember(String memberName) {
		return application.getMember(getName(), memberName);
	}

	@Override
	public Set<String> getValidPlans() {
		return plans;
	}

	@Override
	public boolean isValidForPlan(String plan) {
		return plans.contains(plan);
	}

}