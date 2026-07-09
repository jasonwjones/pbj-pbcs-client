package com.jasonwjones.pbcs.client;

// likely needs to be expanded with other items from: https://docs.oracle.com/en/cloud/saas/enterprise-performance-management-common/prest/get_job_definitions.html
public enum PbcsJobType {

	CUBE_REFRESH("Cube Refresh"),

	EXPORT_DATA("Export Data"),

	PLAN_TYPE_MAP("Plan Type Map"),

	RULES("Rules"),

    CLEAR_CUBE("Clear Cube"),

	OTHER("Other");

	private final String description;

	PbcsJobType(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public static PbcsJobType parse(String type) {
		for (PbcsJobType knownJobType : values()) {
			if (knownJobType.getDescription().equalsIgnoreCase(type)) {
				return knownJobType;
			}
		}
		return OTHER;
	}

}