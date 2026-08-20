package com.jasonwjones.pbcs.api.v3;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for REST API request payloads that launch a job by type and name, optionally with additional
 * parameters.
 */
public abstract class Payload {

	private String jobType;
	private String jobName;
	private Map<String, String> parameters;

	/**
	 * Constructs an instance with the given job type and name.
	 *
	 * @param jobType the job type
	 * @param jobName the job name
	 */
	public Payload(String jobType, String jobName) {
		this.jobType = jobType;
		this.jobName = jobName;
	}

	/**
	 * Gets the job type.
	 *
	 * @return the job type
	 */
	public String getJobType() {
		return jobType;
	}

	/**
	 * Sets the job type.
	 *
	 * @param jobType the job type
	 */
	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	/**
	 * Gets the job name.
	 *
	 * @return the job name
	 */
	public String getJobName() {
		return jobName;
	}

	/**
	 * Sets the job name.
	 *
	 * @param jobName the job name
	 */
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	/**
	 * Sets the additional parameters for this payload. A defensive, mutable copy is made, and the job type
	 * and job name are added to it (the REST API appears to expect them repeated there).
	 *
	 * @param parameters the parameters, may be null
	 */
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

	/**
	 * Gets the additional parameters for this payload.
	 *
	 * @return the parameters
	 */
	public Map<String, String> getParameters() {
		return this.parameters;
	}
}
