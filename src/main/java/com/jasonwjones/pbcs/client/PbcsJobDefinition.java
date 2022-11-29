package com.jasonwjones.pbcs.client;

/**
 * Represents a job definition that exists in the application.
 */
public interface PbcsJobDefinition {

	/**
	 * The job type, such as <code>Cube Refresh</code> or <code>Rules</code>.
	 *
	 * @return the job type
	 */
	String getJobType();

	/**
	 * The job name, such as <code>RefreshCube</code> or <code>calcall</code>.
	 *
	 * @return the job name
	 */
	String getJobName();

	/**
	 * Gets the plan type name that this job is specific to, null if none (such as the Cube Refresh job)
	 *
	 * @return the plan type name, null if none
	 */
	String getPlanTypeName();

}