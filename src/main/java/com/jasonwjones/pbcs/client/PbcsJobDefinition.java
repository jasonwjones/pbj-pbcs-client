package com.jasonwjones.pbcs.client;

/**
 * Represents a job definition that exists in the application.
 */
public interface PbcsJobDefinition extends PbcsObject {

	/**
     * The job type, such as <code>Cube Refresh</code> or <code>Rules</code>. Note: not all job types are currently
     * mapped in to the job type enumeration. A full list can be found here: <a href="https://docs.oracle.com/en/cloud/saas/enterprise-performance-management-common/prest/get_job_definitions.html#planning_rest_apis_104">...</a>.
     *
     * <p>If you need to check for a type that is not currently mapped in to the enumeration, then use the {@link #getOriginalJobType()}
     * method and check the string manually.
     *
     * @return the job type (if known and mapped), otherwise the type {@link PbcsJobType#OTHER}.
     */
	PbcsJobType getJobType();

	/**
	 * Gets the original job type string description. Use when/if the type you need isn't currently modeled in the type
	 * enumeration.
	 *
	 * @return the original job type from the server
	 */
	String getOriginalJobType();

	/**
	 * Gets the plan type name that this job is specific to, null if none (such as the Cube Refresh job)
	 *
	 * @return the plan type name, null if none
	 */
	String getPlanTypeName();

}