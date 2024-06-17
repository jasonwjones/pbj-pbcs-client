package com.jasonwjones.pbcs;

import com.jasonwjones.pbcs.client.*;
import com.jasonwjones.pbcs.client.exceptions.PbcsClientException;
import com.jasonwjones.pbcs.client.impl.AbstractPbcsObject;
import com.jasonwjones.pbcs.client.impl.PbcsPlanningClientImpl;
import com.jasonwjones.pbcs.client.impl.RestContext;
import org.springframework.util.Assert;

import java.util.List;

public class PbcsClientImpl extends AbstractPbcsObject implements PbcsClient {

	private final PbcsPlanningClient planningClient;

	public PbcsClientImpl(RestContext context, PbcsConnection connection, PbcsServiceConfiguration serviceConfiguration) {
		super(context);
		this.planningClient = new PbcsPlanningClientImpl(context, connection.getServer(), serviceConfiguration.isSkipApiCheck());
	}

	@Override
	public PbcsApi getApi() {
		return planningClient.getApi();
	}

	@Override
	public String getServer() {
		return planningClient.getServer();
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

	@Override
	public List<PbcsApplication> getApplications() {
		return planningClient.getApplications();
	}

	@Override
	public PbcsApplication getApplication(String applicationName) throws PbcsClientException {
		Assert.notNull(applicationName, "The application must not be null");
		return planningClient.getApplication(applicationName);
	}

}