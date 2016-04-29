package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.client.PbcsServiceConfiguration;

public class PbcsServiceConfigurationImpl implements PbcsServiceConfiguration {

	private String scheme;

	private Integer port;

	private String planningApiVersion;

	private String planningRestApiPath;

	private String interopApiVersion;
	
	private String interopRestApiPath;
	
	@Override
	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	@Override
	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
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

}
