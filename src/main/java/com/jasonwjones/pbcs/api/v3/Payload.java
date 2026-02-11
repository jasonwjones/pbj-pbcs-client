package com.jasonwjones.pbcs.api.v3;

import java.util.HashMap;
import java.util.Map;

public abstract class Payload {

	private String jobType;
	private String jobName;
	private Map<String, String> parameters;

	public Payload(String jobType, String jobName) {
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

	public void setParameters(Map<String, String> parameters) {
		if (parameters == null) {
			this.parameters = new HashMap<>();
		} else {
            // ensure that we have a mutable map
			this.parameters = new HashMap<>(parameters);
		}
        // for some reason, the jobType and jobName are being repeated in the parameters
		this.parameters.put("jobType", jobType);
		this.parameters.put("jobName", jobName);
	}

	public Map<String, String> getParameters() {
		return this.parameters;
	}
}