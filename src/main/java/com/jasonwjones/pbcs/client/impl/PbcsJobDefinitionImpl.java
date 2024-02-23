package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.api.v3.JobDefinition;
import com.jasonwjones.pbcs.client.PbcsJobDefinition;
import com.jasonwjones.pbcs.client.PbcsJobType;

public class PbcsJobDefinitionImpl extends AbstractPbcsObject implements PbcsJobDefinition {

	private final JobDefinition jobDefinition;

	private final PbcsJobType jobType;

	public PbcsJobDefinitionImpl(RestContext context, JobDefinition jobDefinition) {
		super(context);
		this.jobDefinition = jobDefinition;
		this.jobType = PbcsJobType.parse(jobDefinition.getJobType());
	}

	@Override
	public PbcsJobType getJobType() {
		return jobType;
	}

	@Override
	public String getOriginalJobType() {
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
		return String.format("PbcsJobDefinitionImpl [name=%s, type=%s]", getJobName(), jobType);
	}

}