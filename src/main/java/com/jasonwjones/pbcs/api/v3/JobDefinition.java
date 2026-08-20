package com.jasonwjones.pbcs.api.v3;

/**
 * Maps a single job definition as returned by the REST API's job definitions endpoint.
 */
public class JobDefinition {

	private String jobType;

	private String jobName;

	private String planTypeName;

	/**
	 * Constructs an empty instance for deserialization.
	 */
	public JobDefinition() {
	}

	/**
	 * Gets the type of this job.
	 *
	 * @return the job type
	 */
	public String getJobType() {
		return jobType;
	}

	/**
	 * Sets the type of this job.
	 *
	 * @param jobType the job type
	 */
	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	/**
	 * Gets the name of this job.
	 *
	 * @return the job name
	 */
	public String getJobName() {
		return jobName;
	}

	/**
	 * Sets the name of this job.
	 *
	 * @param jobName the job name
	 */
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	/**
	 * Gets the name of the plan type this job is specific to, if any.
	 *
	 * @return the plan type name, may be null
	 */
	public String getPlanTypeName() {
		return planTypeName;
	}

	/**
	 * Sets the name of the plan type this job is specific to.
	 *
	 * @param planTypeName the plan type name
	 */
	public void setPlanTypeName(String planTypeName) {
		this.planTypeName = planTypeName;
	}

}
