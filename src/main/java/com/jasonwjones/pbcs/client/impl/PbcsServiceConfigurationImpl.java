package com.jasonwjones.pbcs.client.impl;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.client.ClientHttpRequestFactory;

import com.jasonwjones.pbcs.client.PbcsConnection;
import com.jasonwjones.pbcs.client.PbcsServiceConfiguration;

public class PbcsServiceConfigurationImpl implements PbcsServiceConfiguration {

	private String scheme;

	private Integer port;

	private String planningApiVersion;

	private String planningRestApiPath;

	private String interopApiVersion;
	
	private String interopRestApiPath;
	
	private Boolean skipApiCheck = false;
	
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
	
	public Boolean isSkipApiCheck() {
		return skipApiCheck;
	}

	public void setSkipApiCheck(Boolean skipApiCheck) {
		this.skipApiCheck = skipApiCheck;
	}

	@Override
	public ClientHttpRequestFactory createRequestFactory(PbcsConnection connection) {
		HttpClient httpClient = HttpClients.createDefault();
		final HttpHost httpHost = new HttpHost(connection.getServer(), port, scheme);
		final String fullUsername = connection.getIdentityDomain() + "." + connection.getUsername();
		final AuthHttpComponentsClientHttpRequestFactory requestFactory = new AuthHttpComponentsClientHttpRequestFactory(
				httpClient, httpHost, fullUsername, connection.getPassword());
		return requestFactory;
	}
	
}
