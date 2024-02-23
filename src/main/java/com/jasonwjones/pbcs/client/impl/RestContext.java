package com.jasonwjones.pbcs.client.impl;

import org.springframework.web.client.RestTemplate;

public class RestContext {

	private final RestTemplate template;

	private final String baseUrl;

	private final String aifBaseUrl;

	public RestContext(RestTemplate template, String baseUrl, String aifBaseUrl) {
		this.template = template;
		this.baseUrl = baseUrl;
		this.aifBaseUrl = aifBaseUrl;
	}

	public RestTemplate getTemplate() {
		return template;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public String getAifBaseUrl() {
		return this.aifBaseUrl;
	}

	// Trying to carge the AIF/Interop stuff out of the 'core' EPM cloud API
	@Deprecated
	public String getAifUrl(String suffix) {
		return this.aifBaseUrl + suffix;
	}

}