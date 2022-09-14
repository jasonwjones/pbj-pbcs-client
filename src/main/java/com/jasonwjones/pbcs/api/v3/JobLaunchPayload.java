package com.jasonwjones.pbcs.api.v3;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonValue;

public class JobLaunchPayload {

	private String jobType;
	
	private String jobName;

	private Map<String, String> parameters;

	public JobLaunchPayload() {
	}
	
	public JobLaunchPayload(String jobType, String jobName) {
		this.jobType = jobType;
		this.jobName = jobName;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	@JsonValue
	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
		this.parameters.put("jobType", jobType);
		this.parameters.put("jobName", jobName);
	}
	
}
