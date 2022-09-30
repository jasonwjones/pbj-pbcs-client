package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.api.v3.JobLaunchResponse;
import com.jasonwjones.pbcs.client.PbcsJobStatus;
import com.jasonwjones.pbcs.client.PbcsJobStatusCode;

public class PbcsJobStatusImpl implements PbcsJobStatus {

	private final JobLaunchResponse jobLaunchResponse;

	private final PbcsJobStatusCode statusCode;

	public PbcsJobStatusImpl(JobLaunchResponse jobLaunchResponse) {
		this.jobLaunchResponse = jobLaunchResponse;
		this.statusCode = PbcsJobStatusCode.valueOf(jobLaunchResponse.getStatus());
	}

	@Override
	public PbcsJobStatusCode getJobStatusCode() {
		return statusCode;
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
	public String getDetails() {
		return jobLaunchResponse.getDetails();
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
		return "JobLaunchResponse [status=" + jobLaunchResponse.getStatus() + ", details=" + jobLaunchResponse.getDetails() + ", jobId=" + jobLaunchResponse.getJobId() + ", jobName="
				+ jobLaunchResponse.getJobName() + ", descriptiveStatus=" + jobLaunchResponse.getDescriptiveStatus() + "]";
	}


}