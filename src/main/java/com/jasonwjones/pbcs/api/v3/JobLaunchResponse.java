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

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
		// seems like some statuses come in with newlines, this cleans it up at the end 
		if (details != null) { this.details = details.trim(); }
	}

	public Integer getJobId() {
		return jobId;
	}

	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getDescriptiveStatus() {
		return descriptiveStatus;
	}

	public void setDescriptiveStatus(String descriptiveStatus) {
		this.descriptiveStatus = descriptiveStatus;
	}

	@Override
	public String toString() {
		return "JobLaunchResponse [status=" + status + ", details=" + details + ", jobId=" + jobId + ", jobName="
				+ jobName + ", descriptiveStatus=" + descriptiveStatus + "]";
	}

}
