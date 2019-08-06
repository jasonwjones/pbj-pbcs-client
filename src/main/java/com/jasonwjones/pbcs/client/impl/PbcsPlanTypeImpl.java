package com.jasonwjones.pbcs.client.impl;

import java.util.List;

import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsDimension;
import com.jasonwjones.pbcs.client.PbcsPlanType;

public class PbcsPlanTypeImpl implements PbcsPlanType {

	private PbcsApplication application;
	
	private String planType;
	
	PbcsPlanTypeImpl(PbcsApplication application, String planType) {
		this.application = application;
		this.planType = planType;
	}
	
	@Override
	public String getName() {
		return this.planType;
	}

	@Override
	public List<PbcsDimension> getDimensions() {
		return application.getDimensions(planType);
	}

	@Override
	public PbcsApplication getApplication() {
		return this.application;
	}
	
}
