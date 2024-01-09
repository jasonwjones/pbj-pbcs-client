package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.client.PbcsServiceConfiguration;

public class PbcsServiceConfigurationImpl implements PbcsServiceConfiguration {

	private String scheme;

	private String planningApiVersion;

	private String planningRestApiPath;

	private String interopApiVersion;

	private String interopRestApiPath;

	private String aifRestApiVersion;

	private String aifRestApiPath;

	private boolean skipApiCheck = false;

	@Override
	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	@Override
	public String getPlanningApiVersion() {
		return planningApiVersion;
	}

	public void setPlanningApiVersion(String planningApiVersion) {
		this.planningApiVersion = planningApiVersion;
	}

	@Override
	public String getPlanningRestApiPath() {
		return planningRestApiPath;
	}

	public void setPlanningRestApiPath(String planningRestApiPath) {
		this.planningRestApiPath = planningRestApiPath;
	}

	@Override
	public String getInteropApiVersion() {
		return interopApiVersion;
	}

	public void setInteropApiVersion(String interopApiVersion) {
		this.interopApiVersion = interopApiVersion;
	}

	@Override
	public String getInteropRestApiPath() {
		return interopRestApiPath;
	}

	public void setInteropRestApiPath(String interopRestApiPath) {
		this.interopRestApiPath = interopRestApiPath;
	}

	public boolean isSkipApiCheck() {
		return skipApiCheck;
	}

	public void setSkipApiCheck(boolean skipApiCheck) {
		this.skipApiCheck = skipApiCheck;
	}

	@Override
	public String getAifRestApiVersion() {
		return aifRestApiVersion;
	}

	public void setAifRestApiVersion(String aifRestApiVersion) {
		this.aifRestApiVersion = aifRestApiVersion;
	}

	@Override
	public String getAifRestApiPath() {
		return aifRestApiPath;
	}

	public void setAifRestApiPath(String aifRestApiPath) {
		this.aifRestApiPath = aifRestApiPath;
	}

}