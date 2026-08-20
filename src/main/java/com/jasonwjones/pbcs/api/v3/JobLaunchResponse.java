package com.jasonwjones.pbcs.api.v3;

/**
 * Also semantically the same as a Job Status. Might want to look at renaming
 * this...
 *
 * @author jasonwjones
 *
 */
public class JobLaunchResponse {

	private Integer status;

	private String details;

	private Integer jobId;

	private String jobName;

	private String descriptiveStatus;

	// also has "links" -- may want to subclass frm the abstract

	/**
	 * Constructs an empty instance for deserialization.
	 */
	public JobLaunchResponse() {
	}

	/**
	 * Gets the numeric status code of the launched job.
	 *
	 * @return the status code
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * Sets the numeric status code of the launched job.
	 *
	 * @param status the status code
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 * Gets any additional details for this job launch, such as an error message.
	 *
	 * @return the details
	 */
	public String getDetails() {
		return details;
	}

	/**
	 * Sets additional details for this job launch.
	 *
	 * @param details the details
	 */
	public void setDetails(String details) {
		this.details = details;
		// seems like some statuses come in with newlines, this cleans it up at the end
		if (details != null) { this.details = details.trim(); }
	}

	/**
	 * Gets the ID of the launched job.
	 *
	 * @return the job ID
	 */
	public Integer getJobId() {
		return jobId;
	}

	/**
	 * Sets the ID of the launched job.
	 *
	 * @param jobId the job ID
	 */
	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}

	/**
	 * Gets the name of the launched job.
	 *
	 * @return the job name
	 */
	public String getJobName() {
		return jobName;
	}

	/**
	 * Sets the name of the launched job.
	 *
	 * @param jobName the job name
	 */
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	/**
	 * Gets a human-readable status description for the launched job.
	 *
	 * @return the descriptive status
	 */
	public String getDescriptiveStatus() {
		return descriptiveStatus;
	}

	/**
	 * Sets the human-readable status description for the launched job.
	 *
	 * @param descriptiveStatus the descriptive status
	 */
	public void setDescriptiveStatus(String descriptiveStatus) {
		this.descriptiveStatus = descriptiveStatus;
	}

	@Override
	public String toString() {
		return "JobLaunchResponse [status=" + status + ", details=" + details + ", jobId=" + jobId + ", jobName="
				+ jobName + ", descriptiveStatus=" + descriptiveStatus + "]";
	}

}
