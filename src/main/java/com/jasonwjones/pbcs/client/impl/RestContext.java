package com.jasonwjones.pbcs.client.impl;

import org.springframework.web.client.RestTemplate;

public class RestContext {

	private RestTemplate template;
	
	private String baseUrl;
	
	public RestContext(RestTemplate template, String baseUrl) {
		this.template = template;
		this.baseUrl = baseUrl;
	}

	public RestTemplate getTemplate() {
		return template;
	}

	public String getBaseUrl() {
		return baseUrl;
	}
	
}
