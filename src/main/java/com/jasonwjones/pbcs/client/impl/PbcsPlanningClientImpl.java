package com.jasonwjones.pbcs.client.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import com.jasonwjones.pbcs.api.v3.Api;
import com.jasonwjones.pbcs.api.v3.Application;
import com.jasonwjones.pbcs.api.v3.Applications;
import com.jasonwjones.pbcs.client.PbcsApi;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsConnection;
import com.jasonwjones.pbcs.client.PbcsPlanningClient;
import com.jasonwjones.pbcs.client.PbcsServiceConfiguration;
import com.jasonwjones.pbcs.client.exceptions.PbcsClientException;

/**
 * Default implementation of PbcsPlanningClient. This class can be thought of as
 * the entry point to the Planning REST API. Most users will likely jump
 * straight from this class to grabbing an instance of {@link PbcsApplication},
 * which is the main interface for modeling operations on a particular
 * application.
 * 
 * @author Jason Jones
 *
 */
public class PbcsPlanningClientImpl implements PbcsPlanningClient {

	private static final Logger logger = LoggerFactory.getLogger(PbcsPlanningClientImpl.class);
	
	private RestContext context;

	private RestTemplate restTemplate;

	private String baseUrl;

	private PbcsServiceConfiguration serviceConfig;
	
	// TODO: Provide option to skip initial check (default should check)
	public PbcsPlanningClientImpl(PbcsConnection connection, PbcsServiceConfiguration serviceConfiguration) throws PbcsClientException {
		this.serviceConfig = serviceConfiguration;
		this.baseUrl = serviceConfig.getScheme() + "://" + connection.getServer() + serviceConfig.getPlanningRestApiPath() + serviceConfig.getPlanningApiVersion() + "/";
		this.restTemplate = new RestTemplate(serviceConfiguration.createRequestFactory(connection));
		this.restTemplate.setErrorHandler(new MyResponseErrorHandler());
		ClientHttpRequestInterceptor interceptor = new RequestResponseLoggingInterceptor();
		this.restTemplate.setInterceptors(Collections.singletonList(interceptor));
		
		this.context = new RestContext(restTemplate, baseUrl);
		this.context.setAifBaseUrl(serviceConfig.getScheme() + "://" + connection.getServer() + serviceConfiguration.getAifRestApiPath() + serviceConfiguration.getAifRestApiVersion());

		// perform a call to the API to validate that we are actually connected
		// if this doesn't work then we can throw an exception to the caller and
		// they'll know the connection didn't work
		
		if (!serviceConfiguration.isSkipApiCheck()) {
			try {
				PbcsApi api = getApi();
				if (!api.isLatest()) {
					logger.warn("PBCS indicates that the current API ({}) is not the latest available", api.getVersion());
				} else {
					logger.info("PBCS indicated that the current API ({}) is latest available", api.getVersion());
				}
			} catch (PbcsClientException e) {
				logger.error("Problem initializing PBCS API. This likely means the server name or a connection parameter is invalid.");
				throw e;
			}
		} else {
			logger.debug("Skipping initialization API check");
		}
	}

	@Override
	public PbcsApi getApi() {
		logger.info("Checking API from base URL {}", baseUrl);
		ResponseEntity<Api> checkApi = restTemplate.getForEntity(baseUrl, Api.class);
		return new PbcsApiImpl(checkApi.getBody());
	}

	public String get(String url) {
		logger.info("Getting from test URL", url);
		ResponseEntity<String> checkApi = restTemplate.getForEntity(baseUrl + url, String.class);
		return checkApi.getBody();
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
