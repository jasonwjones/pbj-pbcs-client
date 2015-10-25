package com.jasonwjones.pbcs.api.v3;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Api {

	private String version;
	
	private String lifecycle;
	
	private boolean isLatest;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getLifecycle() {
		return lifecycle;
	}

	public void setLifecycle(String lifecycle) {
		this.lifecycle = lifecycle;
	}

	@JsonProperty("isLatest")
	public boolean isLatest() {
		return isLatest;
	}

	@JsonProperty("isLatest")
	public void setLatest(boolean isLatest) {
		this.isLatest = isLatest;
	}

	@Override
	public String toString() {
		return "Api [version=" + version + ", lifecycle=" + lifecycle + ", isLatest=" + isLatest + "]";
	}
	
}
