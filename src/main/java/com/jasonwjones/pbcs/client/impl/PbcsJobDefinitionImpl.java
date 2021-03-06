package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.api.v3.JobDefinition;
import com.jasonwjones.pbcs.client.PbcsJobDefinition;

public class PbcsJobDefinitionImpl implements PbcsJobDefinition {

	private RestContext context;
	
	private JobDefinition jobDefinition;
	
	public PbcsJobDefinitionImpl(RestContext context, JobDefinition jobDefinition) {
		this.context = context;
		this.jobDefinition = jobDefinition;
	}

	@Override
	public String getJobType() {
		return jobDefinition.getJobType();
	}

	@Override
	public String getJobName() {
		return jobDefinition.getJobName();
	}
	
}
