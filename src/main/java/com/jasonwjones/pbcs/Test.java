package com.jasonwjones.pbcs;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.jasonwjones.pbcs.api.v3.Application;
import com.jasonwjones.pbcs.api.v3.Applications;

public class Test {

	public static void main(String[] args) {
		
		String endpoint = "foo";
		String version = "11.1.2.3.600";
		String server = "https://plantastic-test-keyperformanceideas.pbcs.us2.oraclecloud.com/workspace";
		
		//restTemplate.getForObject(endpoint + "￼￼￼￼￼￼￼￼￼￼￼￼￼￼￼￼￼￼￼￼￼￼￼￼/HyperionPlanning/rest/{version}/applications/{appName}/jobs", Object.class);
		HttpClient httpClient = HttpClientBuilder.create().build();
		
		HttpHost httpHost = new HttpHost("plantastic-test-keyperformanceideas.pbcs.us2.oraclecloud.com", 443, "https");
		//new HttpHost()
		final AuthHttpComponentsClientHttpRequestFactory requestFactory =
			    new AuthHttpComponentsClientHttpRequestFactory(
			                httpClient, httpHost, "keyperformanceideas.jjones@keyperformanceideas.com", "Ja$on200");
		
		// INITIAL POST
		//String testPost = "https://plantastic-test-keyperformanceideas.pbcs.us2.oraclecloud.com/HyperionPlanning";
		String testPost = "https://plantastic-test-keyperformanceideas.pbcs.us2.oraclecloud.com/HyperionPlanning/rest/v3/applications";
		
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		//ResponseEntity<String> result = restTemplate.getForEntity(testPost, String.class, "s");
		ResponseEntity<Applications> result = restTemplate.getForEntity(testPost, Applications.class, "s");

		System.out.println("Body: " );
		System.out.println(result.getBody());
		Applications apps = result.getBody();
		System.out.println();
		
		for (Application application : apps.getItems()) {
			System.out.println("Application: " + application.getName());
			System.out.println("Type: " + application.getType());
		}
		
	}

}
