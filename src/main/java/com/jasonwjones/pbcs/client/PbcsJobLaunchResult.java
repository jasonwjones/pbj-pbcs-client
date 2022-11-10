package com.jasonwjones.pbcs.client;

public interface PbcsJobLaunchResult {

	/**
	 * Status for job:
	 *
	 * <pre>
	 * -1 = in progress
	 *  0 = success
	 *  1 = error
	 *  2 = cancel pending
	 *  3 = cancelled
	 *  4 = invalid parameter
	 *  Integer.MAX_VALUE = unknown
	 *  </pre>
	 *
	 * @return the numeric status for the job, as defined by the PBCS REST API specs
	 */
	Integer getStatus();

	/**
	 * Typically "Completed" or "Error"
	 *
	 * @return the descriptive status of result
	 */
	String getDescriptiveStatus();

	/**
	 * Typically three digits
	 *
	 * @return the job ID
	 */
	Integer getJobId();

	String getJobName();

	void waitUntilFinished();

}