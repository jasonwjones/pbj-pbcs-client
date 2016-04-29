package com.jasonwjones.pbcs.client.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.jasonwjones.pbcs.api.v3.Api;
import com.jasonwjones.pbcs.api.v3.Application;
import com.jasonwjones.pbcs.api.v3.Applications;
import com.jasonwjones.pbcs.client.PbcsApi;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsConnection;
import com.jasonwjones.pbcs.client.PbcsPlanningClient;
import com.jasonwjones.pbcs.client.exceptions.PbcsClientException;

public class PbcsPlanningClientImpl implements PbcsPlanningClient {

	private RestContext context;
	
	private RestTemplate restTemplate;

	private String baseUrl;
	
	private PbcsApi api;
	
	private static String PATH = "/HyperionPlanning/rest/";
	
	private static String SCHEME = "https";
	
	private static int PORT = 443;
	
	private static String defaultVersion = "v3";
	
	public PbcsPlanningClientImpl(PbcsConnection connection) {
		
	}
	
	public PbcsPlanningClientImpl(String server, String identityDomain, String username, String password) throws PbcsClientException {
		HttpClient httpClient = HttpClients.createDefault();

		final HttpHost httpHost = new HttpHost(server, PORT, SCHEME);
		final String fullUsername = identityDomain + "." + username;
		final AuthHttpComponentsClientHttpRequestFactory requestFactory = new AuthHttpComponentsClientHttpRequestFactory(
				httpClient, httpHost, fullUsername, password);
		
		this.baseUrl = SCHEME + "://" + server + PATH + defaultVersion + "/";
		this.restTemplate = new RestTemplate(requestFactory);
		this.context = new RestContext(restTemplate, baseUrl);
		
		// perform a call to the API to validate that we are actually connected
		// if this doesn't work then we can throw an exception to the caller and they'll 
		// know the connection didn't work
		ResponseEntity<Api> checkApi = restTemplate.getForEntity(baseUrl, Api.class);
		this.api = new PbcsApiImpl(checkApi.getBody());
	}

	@Override
	public PbcsApi getApi() {
		return this.api;
	}
	
	@Override
	public List<PbcsApplication> getApplications() {
		String url = this.baseUrl + "applications";
		ResponseEntity<Applications> result = restTemplate.getForEntity(url, Applications.class);

		List<PbcsApplication> pbcsApplications = new ArrayList<PbcsApplication>();
		for (Application application : result.getBody().getItems()) {
			PbcsApplicationImpl appImpl = new PbcsApplicationImpl(context, application);
			pbcsApplications.add(appImpl);
		}
		return pbcsApplications;
	}

	@Override
	public PbcsApplication getApplication(String applicationName) throws PbcsClientException {
		List<PbcsApplication> applications = getApplications();
		for (PbcsApplication application : applications) {
			if (application.getName().equalsIgnoreCase(applicationName)) {
				return application;
			}
		}
		throw new PbcsClientException("No application with name: " + applicationName);
	}

}
