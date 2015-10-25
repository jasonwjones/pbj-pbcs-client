package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.api.v3.JobLaunchResponse;
import com.jasonwjones.pbcs.client.PbcsJobLaunchResult;

public class PbcsJobLaunchResultImpl implements PbcsJobLaunchResult {

	private JobLaunchResponse jobLaunchResponse;
	
	public PbcsJobLaunchResultImpl(JobLaunchResponse jobLaunchResponse) {
		this.jobLaunchResponse = jobLaunchResponse;
	}
	
	@Override
	public Integer getStatus() {
		return jobLaunchResponse.getStatus();
	}

	@Override
	public String getDescriptiveStatus() {
		return jobLaunchResponse.getDescriptiveStatus();
	}

	@Override
	public Integer getJobId() {
		return jobLaunchResponse.getJobId();
	}

	@Override
	public String getJobName() {
		return jobLaunchResponse.getJobName();
	}

	@Override
	public String toString() {
		return jobLaunchResponse.toString();
	}
	
}
