package com.jasonwjones.pbcs.client;

public enum PbcsJobType {

	CUBE_REFRESH("Cube Refresh"), EXPORT_DATA("Export Data"), PLAN_TYPE_MAP("Plan Type Map"), RULES("Rules");
	
	private String description;

	private PbcsJobType(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
