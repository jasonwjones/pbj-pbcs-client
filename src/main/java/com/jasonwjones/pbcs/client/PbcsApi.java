package com.jasonwjones.pbcs.client;

/**
 * Simple model for current REST API information
 */
public interface PbcsApi {

	/**
	 * The current version of the API
	 *
	 * @return the current API version
	 */
	String getVersion();

	/**
	 * Lifecycle status. Per documentation, possible values are <code>active</code> and <code>deprecated</code>.
	 *
	 * @return the lifecycle value
	 */
	String getLifecycle();

	/**
	 * Returns true if the API information endpoint indicates this is the latest version of the API. For the last several
	 * years this has been true and the API has been on version 3.
	 *
	 * @return true if this is the latest API, false otherwise
	 */
	boolean isLatest();

}