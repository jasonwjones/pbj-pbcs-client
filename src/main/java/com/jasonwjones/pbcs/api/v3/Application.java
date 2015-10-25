package com.jasonwjones.pbcs.api.v3;

public class Application {

	private boolean dpEnabled;
	
	private String name;
	
	private String type;

	public boolean isDpEnabled() {
		return dpEnabled;
	}

	public void setDpEnabled(boolean dpEnabled) {
		this.dpEnabled = dpEnabled;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
