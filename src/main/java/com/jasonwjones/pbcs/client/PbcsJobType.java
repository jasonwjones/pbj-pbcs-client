package com.jasonwjones.pbcs.client;

// likely needs to be expanded with other items from: https://docs.oracle.com/en/cloud/saas/enterprise-performance-management-common/prest/get_job_definitions.html
/**
 * The known job types returned by the REST API's job definitions endpoint.
 */
public enum PbcsJobType {

	/**
	 * A cube refresh job.
	 */
	CUBE_REFRESH("Cube Refresh"),

	/**
	 * A data export job.
	 */
	EXPORT_DATA("Export Data"),

	/**
	 * A plan type map job.
	 */
	PLAN_TYPE_MAP("Plan Type Map"),

	/**
	 * A business rule job.
	 */
	RULES("Rules"),

	/**
	 * A clear cube job.
	 */
    CLEAR_CUBE("Clear Cube"),

	/**
	 * A job type not otherwise recognized by this library.
	 */
	OTHER("Other");

	private final String description;

	PbcsJobType(String description) {
		this.description = description;
	}

	/**
	 * Gets a human-readable description of this job type.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Parses the job type from its REST API description, matched case-insensitively.
	 *
	 * @param type the job type description
	 * @return the matching enum value, or {@link #OTHER} if not recognized
	 */
	public static PbcsJobType parse(String type) {
		for (PbcsJobType knownJobType : values()) {
			if (knownJobType.getDescription().equalsIgnoreCase(type)) {
				return knownJobType;
			}
		}
		return OTHER;
	}

}
