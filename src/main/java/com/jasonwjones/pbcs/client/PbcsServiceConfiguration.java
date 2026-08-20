package com.jasonwjones.pbcs.client;

/**
 * Configuration for the REST endpoints and API versions this library talks to: the Planning REST API, the
 * unofficial data management (DM/AIF) REST API, and the interop (LCM) REST API.
 */
public interface PbcsServiceConfiguration {

	/**
	 * Gets the URI scheme to use, e.g. "https".
	 *
	 * @return the scheme
	 */
	String getScheme();

	/**
	 * Gets the Planning REST API version to use, e.g. "v3".
	 *
	 * @return the Planning API version
	 */
	String getPlanningApiVersion();

	/**
	 * Gets the base path of the Planning REST API, e.g. "/HyperionPlanning/rest/".
	 *
	 * @return the Planning REST API path
	 */
	String getPlanningRestApiPath();

	/**
	 * Gets the interop (LCM) REST API version to use.
	 *
	 * @return the interop API version
	 */
	String getInteropApiVersion();

	/**
	 * Gets the base path of the interop (LCM) REST API.
	 *
	 * @return the interop REST API path
	 */
	String getInteropRestApiPath();

	/**
	 * Gets the data management (DM/AIF) REST API version to use.
	 *
	 * @return the AIF API version
	 */
	String getAifRestApiVersion();

	/**
	 * Gets the base path of the data management (DM/AIF) REST API.
	 *
	 * @return the AIF REST API path
	 */
	String getAifRestApiPath();

	/**
	 * Whether the initial "is this the latest API" check should be skipped.
	 *
	 * @return true if the API check should be skipped, false otherwise
	 */
	boolean isSkipApiCheck();

}
