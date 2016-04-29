package com.jasonwjones.pbcs;

import java.io.File;
import java.util.List;

import com.jasonwjones.pbcs.client.PbcsApi;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsConnection;
import com.jasonwjones.pbcs.client.PbcsPlanningClient;
import com.jasonwjones.pbcs.client.PbcsServiceConfiguration;
import com.jasonwjones.pbcs.client.exceptions.PbcsClientException;
import com.jasonwjones.pbcs.client.impl.PbcsPlanningClientImpl;
import com.jasonwjones.pbcs.interop.InteropClient;
import com.jasonwjones.pbcs.interop.impl.ApplicationSnapshot;
import com.jasonwjones.pbcs.interop.impl.InteropClientImpl;

public class PbcsClientImpl implements PbcsClient {

	private PbcsPlanningClient planningClient;
	
	private InteropClient interopClient;
	
	public PbcsClientImpl(PbcsConnection connection, PbcsServiceConfiguration serviceConfiguration) {
		this.planningClient = new PbcsPlanningClientImpl(connection, serviceConfiguration);
		this.interopClient = new InteropClientImpl(connection, serviceConfiguration);
	}
	
	@Override
	public PbcsApi getApi() {
		return planningClient.getApi();
	}

	@Override
	public List<PbcsApplication> getApplications() {
		return planningClient.getApplications();
	}

	@Override
	public PbcsApplication getApplication(String applicationName) throws PbcsClientException {
		return planningClient.getApplication(applicationName);
	}

	@Override
	public File downloadFile(String filename) throws PbcsClientException {
		return interopClient.downloadFile(filename);
	}

	@Override
	public void uploadFile(String filename) {
		interopClient.uploadFile(filename);
	}

	@Override
	public void deleteFile(String filename) {
		interopClient.deleteFile(filename);
	}

	@Override
	public List<ApplicationSnapshot> listFiles() {
		return interopClient.listFiles();
	}

	@Override
	public void LcmExport() {
		throw new UnsupportedOperationException("Operation not supported yet");
	}

	@Override
	public void LcmImport() {
		throw new UnsupportedOperationException("Operation not supported yet");
	}

}
