package com.jasonwjones.pbcs.client.impl;

import org.springframework.web.client.RestTemplate;

/**
 * Bundles the configured {@link RestTemplate} and base URLs shared by the REST-calling implementation
 * classes in this package.
 */
public class RestContext {

	private final RestTemplate template;

	private final String server;

	private final String baseUrl;

	private final String aifBaseUrl;

	/**
	 * Constructs an instance with the given template and URLs.
	 *
	 * @param template the configured REST template to use for calls
	 * @param server the server name
	 * @param baseUrl the base URL for the Planning REST API
	 * @param aifBaseUrl the base URL for the data management (DM/AIF) REST API
	 */
	public RestContext(RestTemplate template, String server, String baseUrl, String aifBaseUrl) {
		this.template = template;
		this.server = server;
		this.baseUrl = baseUrl;
		this.aifBaseUrl = aifBaseUrl;
	}

	/**
	 * Gets the configured REST template.
	 *
	 * @return the REST template
	 */
	public RestTemplate getTemplate() {
		return template;
	}

	/**
	 * Gets the server name.
	 *
	 * @return the server name
	 */
	public String getServer() {
		return server;
	}

	/**
	 * Gets the base URL for the Planning REST API.
	 *
	 * @return the base URL
	 */
	public String getBaseUrl() {
		return baseUrl;
	}

	// Trying to carve the AIF/Interop stuff out of the 'core' EPM cloud API
	/**
	 * Builds a full URL under the data management (DM/AIF) base URL.
	 *
	 * @param suffix the URL suffix, relative to the AIF base URL
	 * @return the full URL
	 * @deprecated this method is being phased out as AIF/interop calls are carved out of the core EPM cloud API
	 */
	@Deprecated
	public String getAifUrl(String suffix) {
		return this.aifBaseUrl + suffix;
	}

}
