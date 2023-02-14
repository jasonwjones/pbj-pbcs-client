package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.api.v3.JobDefinition;
import com.jasonwjones.pbcs.client.PbcsJobDefinition;

public class PbcsJobDefinitionImpl extends AbstractPbcsObject implements PbcsJobDefinition {

	private final JobDefinition jobDefinition;

	public PbcsJobDefinitionImpl(RestContext context, JobDefinition jobDefinition) {
		super(context);
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

	@Override
	public String getPlanTypeName() {
		return jobDefinition.getPlanTypeName();
	}

	@Override
	public String toString() {
		return String.format("PbcsJobDefinitionImpl [name=%s, type=%s]", getJobName(), getJobType());
	}

}