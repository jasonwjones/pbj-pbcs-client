package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.api.v3.Api;
import com.jasonwjones.pbcs.api.v3.Application;
import com.jasonwjones.pbcs.api.v3.Applications;
import com.jasonwjones.pbcs.client.*;
import com.jasonwjones.pbcs.client.exceptions.PbcsClientException;
import com.jasonwjones.pbcs.client.exceptions.PbcsNoSuchObjectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of PbcsPlanningClient. This class can be thought of as the entry point to the Planning REST
 * API. Most users will likely jump straight from this class to grabbing an instance of {@link PbcsApplication}, which
 * is the main interface for modeling operations on a particular application.
 *
 * @author Jason Jones
 */
public class PbcsPlanningClientImpl extends AbstractPbcsObject implements PbcsPlanningClient {

	private static final Logger logger = LoggerFactory.getLogger(PbcsPlanningClientImpl.class);

	private final PbcsConnection connection;

	private final PbcsApi api;

	/**
	 * Constructs an instance for the given connection and service configuration.
	 *
	 * @param context the REST context to use for calls made by this object
	 * @param connection the connection to use
	 * @param serviceConfiguration the service configuration to use
	 */
	public PbcsPlanningClientImpl(RestContext context, PbcsConnection connection, PbcsServiceConfiguration serviceConfiguration) {
		super(context);
		this.connection = connection;

		if (!serviceConfiguration.isSkipApiCheck()) {
			try {
				api = getApi();
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
			api = null;
		}
	}

	@Override
	public PbcsApi getApi() {
		if (api != null) {
			return api;
		} else {
			logger.info("Checking API for {}", connection.getServer());
			Api checkApi = get("", Api.class);
			return new PbcsApiImpl(checkApi);
		}
	}

	@Override
	public String getServer() {
		return connection.getServer();
	}

	@Override
	public String getUserName() {
		return connection.getUsername();
	}

	@Override
	public List<PbcsApplication> getApplications() {
		Applications result = get("applications", Applications.class);

		List<PbcsApplication> pbcsApplications = new ArrayList<>();
		for (Application application : result.getItems()) {
			PbcsApplicationImpl appImpl = new PbcsApplicationImpl(context, this, application);
			pbcsApplications.add(appImpl);
		}
		return pbcsApplications;
	}

	@Override
	public PbcsApplication getApplication(String applicationName) throws PbcsClientException {
		return getApplication(applicationName, false);
	}

	/**
	 * Gets the application with the given name, optionally skipping the existence check.
	 *
	 * @param applicationName the application name
	 * @param skipCheck true to skip verifying the application exists, false otherwise
	 * @return the application
	 * @throws PbcsClientException if the application does not exist (and skipCheck is false) or another error occurs
	 */
	public PbcsApplication getApplication(String applicationName, boolean skipCheck) throws PbcsClientException {
		Assert.notNull(applicationName, "The application must not be null");
		if (skipCheck) {
			// [name=Vision, type=HP, dpEnabled=false]]
			Application application = new Application();
			application.setName(applicationName);
			application.setType("HP");
			return new PbcsApplicationImpl(context, this, application);
		} else {
			for (PbcsApplication application : getApplications()) {
				if (application.getName().equalsIgnoreCase(applicationName)) {
					return application;
				}
			}
			throw new PbcsNoSuchObjectException(applicationName, PbcsObjectType.APPLICATION);
		}
	}

	@Override
	public String getName() {
		return getServer();
	}

	@Override
	public PbcsObject getParent() {
		return null;
	}

	@Override
	public PbcsObjectType getObjectType() {
		return PbcsObjectType.CLIENT;
	}

}