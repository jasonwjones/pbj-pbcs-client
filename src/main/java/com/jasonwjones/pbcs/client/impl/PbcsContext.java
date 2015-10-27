package com.jasonwjones.pbcs.client.impl;

import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.jasonwjones.pbcs.client.PbcsApi;
import com.jasonwjones.pbcs.client.PbcsClient;
import com.jasonwjones.pbcs.client.exceptions.PbcsNoSuchClientException;
import com.jasonwjones.pbcs.interop.InteropClient;

public class PbcsContext {

	private RestContext context;
	
	private RestTemplate restTemplate;

	private String baseUrl;
	
	public PbcsContext(String server, String identityDomain, String username, String password) {
		HttpClient httpClient = HttpClients.createDefault();

		final HttpHost httpHost = new HttpHost(server, 443, "https");
		final String fullUsername = identityDomain + "." + username;
		final AuthHttpComponentsClientHttpRequestFactory requestFactory = new AuthHttpComponentsClientHttpRequestFactory(
				httpClient, httpHost, fullUsername, password);
				
		this.baseUrl = "https://" + server + "/HyperionPlanning/rest/v4";
		this.restTemplate = new RestTemplate(requestFactory);
		this.context = new RestContext(restTemplate, baseUrl);
		
//		ResponseEntity<RestApiWrapper> apiWrapper = template.getForEntity(baseUrl, RestApiWrapper.class);
//		
//		List<PbcsApi> apis = new ArrayList<PbcsApi>();
//		for (Api api : apiWrapper.getBody().getItems()) {
//			apis.add(new PbcsApiImpl(api));
//		}
//		return apis;

	}
	
	// should be v3 or something
	// output of page will be 'Not Found' if there's no such client
	// note: 404 will be thrown for no such API version (good!)
	public PbcsClient getClient(String version) throws PbcsNoSuchClientException {
		return null;
	}
	
	public InteropClient getInteropClient(String version) {
		// expects 11.1.2.3.600... etc
		return null;
	}
	
	public List<PbcsApi> getApis() {
		ResponseEntity<String> apiWrapper = restTemplate.getForEntity(baseUrl, String.class);
		System.out.println("Code: " + apiWrapper.getStatusCode().value());
		System.out.println("Result: " + apiWrapper.getBody());
		return null;
//		List<PbcsApi> apis = new ArrayList<PbcsApi>();
//		for (Api api : apiWrapper.getBody().getItems()) {
//			apis.add(new PbcsApiImpl(api));
//		}
//		return apis;
	}
}
