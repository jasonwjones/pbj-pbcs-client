package com.jasonwjones.pbcs.client.impl;

import java.util.Set;

import com.jasonwjones.pbcs.aif.AifDimension;
import com.jasonwjones.pbcs.client.PbcsDimension;

public class PbcsDimensionImpl implements PbcsDimension {

	private Set<String> plans;
	
	private AifDimension dimension;
	
	PbcsDimensionImpl(Set<String> plans, AifDimension dimension) {
		this.plans = plans;
		this.dimension = dimension;
	}
	
	@Override
	public String getBalanceColumnName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDimensionClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDimensionClassOrg() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return dimension.getName();
	}

	@Override
	public String getNameOrg() {
		// TODO Auto-generated method stub
		return null;
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
