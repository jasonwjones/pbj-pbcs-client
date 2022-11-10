package com.jasonwjones.pbcs.client;

public interface PbcsJobStatus {

	/**
	 * Returns the numeric status of the job:
	 *
	 * <p>
	 * -1 = in progress 0 = success 1 = error 2 = cancel pending 3 = cancelled 4
	 * = invalid parameter Integer.MAX_VALUE = unknown
	 *
	 * @return the numeric job status
	 */
	Integer getStatus();

	PbcsJobStatusCode getJobStatusCode();

	/**
	 * Returns textual description of the status code, such as Completed or
	 * Error
	 *
	 * @return status text
	 */
	String getDescriptiveStatus();

	String getDetails();

	/**
	 * The ID of the job
	 *
	 * @return the job ID
	 */
	Integer getJobId();

	String getJobName();

}