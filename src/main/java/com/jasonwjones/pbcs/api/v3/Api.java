package com.jasonwjones.pbcs.api.v3;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Describes a single version of the REST API, as returned by the API's root/discovery endpoint.
 */
public class Api {

	private String version;

	/**
	 * Apparently is either "active" or "deprecated"
	 */
	private String lifecycle;

	private boolean isLatest;

	/**
	 * Constructs an empty instance for deserialization.
	 */
	public Api() {
	}

	/**
	 * Gets the API version string.
	 *
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Sets the API version string.
	 *
	 * @param version the version
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Gets the lifecycle state of this API version, e.g. "active" or "deprecated".
	 *
	 * @return the lifecycle state
	 */
	public String getLifecycle() {
		return lifecycle;
	}

	/**
	 * Sets the lifecycle state of this API version.
	 *
	 * @param lifecycle the lifecycle state
	 */
	public void setLifecycle(String lifecycle) {
		this.lifecycle = lifecycle;
	}

	/**
	 * Whether this is the latest available API version.
	 *
	 * @return true if this is the latest version, false otherwise
	 */
	@JsonProperty("isLatest")
	public boolean isLatest() {
		return isLatest;
	}

	/**
	 * Sets whether this is the latest available API version.
	 *
	 * @param isLatest true if this is the latest version, false otherwise
	 */
	@JsonProperty("isLatest")
	public void setLatest(boolean isLatest) {
		this.isLatest = isLatest;
	}

	@Override
	public String toString() {
		return "Api [version=" + version + ", lifecycle=" + lifecycle + ", isLatest=" + isLatest + "]";
	}

}
