package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.client.PbcsServiceConfiguration;

/**
 * Default, mutable {@link PbcsServiceConfiguration} implementation.
 */
public class PbcsServiceConfigurationImpl implements PbcsServiceConfiguration {

	private String scheme;

	private String planningApiVersion;

	private String planningRestApiPath;

	private String interopApiVersion;

	private String interopRestApiPath;

	private String aifRestApiVersion;

	private String aifRestApiPath;

	private boolean skipApiCheck = false;

	/**
	 * Constructs an empty instance.
	 */
	public PbcsServiceConfigurationImpl() {
	}

	@Override
	public String getScheme() {
		return scheme;
	}

	/**
	 * Sets the URI scheme to use.
	 *
	 * @param scheme the scheme
	 */
	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	@Override
	public String getPlanningApiVersion() {
		return planningApiVersion;
	}

	/**
	 * Sets the Planning REST API version to use.
	 *
	 * @param planningApiVersion the Planning API version
	 */
	public void setPlanningApiVersion(String planningApiVersion) {
		this.planningApiVersion = planningApiVersion;
	}

	@Override
	public String getPlanningRestApiPath() {
		return planningRestApiPath;
	}

	/**
	 * Sets the base path of the Planning REST API.
	 *
	 * @param planningRestApiPath the Planning REST API path
	 */
	public void setPlanningRestApiPath(String planningRestApiPath) {
		this.planningRestApiPath = planningRestApiPath;
	}

	@Override
	public String getInteropApiVersion() {
		return interopApiVersion;
	}

	/**
	 * Sets the interop (LCM) REST API version to use.
	 *
	 * @param interopApiVersion the interop API version
	 */
	public void setInteropApiVersion(String interopApiVersion) {
		this.interopApiVersion = interopApiVersion;
	}

	@Override
	public String getInteropRestApiPath() {
		return interopRestApiPath;
	}

	/**
	 * Sets the base path of the interop (LCM) REST API.
	 *
	 * @param interopRestApiPath the interop REST API path
	 */
	public void setInteropRestApiPath(String interopRestApiPath) {
		this.interopRestApiPath = interopRestApiPath;
	}

	@Override
	public boolean isSkipApiCheck() {
		return skipApiCheck;
	}

	/**
	 * Sets whether the initial "is this the latest API" check should be skipped.
	 *
	 * @param skipApiCheck true to skip the check, false otherwise
	 */
	public void setSkipApiCheck(boolean skipApiCheck) {
		this.skipApiCheck = skipApiCheck;
	}

	@Override
	public String getAifRestApiVersion() {
		return aifRestApiVersion;
	}

	/**
	 * Sets the data management (DM/AIF) REST API version to use.
	 *
	 * @param aifRestApiVersion the AIF API version
	 */
	public void setAifRestApiVersion(String aifRestApiVersion) {
		this.aifRestApiVersion = aifRestApiVersion;
	}

	@Override
	public String getAifRestApiPath() {
		return aifRestApiPath;
	}

	/**
	 * This is going to have a value such as
	 * <a href="https://example.pbcs.us2.oraclecloud.com/aif/rest/V1/applications/APP_NAME">example</a>.
	 *
	 * @param aifRestApiPath the AIF path
	 */
	public void setAifRestApiPath(String aifRestApiPath) {
		this.aifRestApiPath = aifRestApiPath;
	}

}