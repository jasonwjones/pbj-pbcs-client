package com.jasonwjones.pbcs;

import java.io.File;
import java.util.List;

import com.jasonwjones.pbcs.client.JobConfiguration;
import com.jasonwjones.pbcs.client.PbcsApi;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsConnection;
import com.jasonwjones.pbcs.client.PbcsPlanningClient;
import com.jasonwjones.pbcs.client.PbcsServiceConfiguration;
import com.jasonwjones.pbcs.client.exceptions.PbcsClientException;
import com.jasonwjones.pbcs.client.impl.PbcsPlanningClientImpl;
import com.jasonwjones.pbcs.interop.InteropClient;
import com.jasonwjones.pbcs.interop.impl.ApplicationSnapshot;
import com.jasonwjones.pbcs.interop.impl.ApplicationSnapshotInfo;
import com.jasonwjones.pbcs.interop.impl.InteropClientImpl;
import org.springframework.web.client.ResponseErrorHandler;

public class PbcsClientImpl implements PbcsClient {

	private PbcsPlanningClient planningClient;
	
	private InteropClient interopClient;
	
	// TODO: Option to defer and lazily initialize
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
	public String LcmImport(JobConfiguration job) {
		return interopClient.LcmImport(job);
	}

	@Override
	public File downloadFile(String filename, String localFilename) throws PbcsClientException {
		return interopClient.downloadFile(filename, localFilename);
	}

	@Override
	public void uploadFile(String filename) {
		interopClient.uploadFile(filename);
	}

	@Override
	public void uploadFile(String filename, String directory) {
		interopClient.uploadFile(filename, directory);
	}

	@Override
	public void deleteFile(String filename) {
		interopClient.deleteFile(filename);
	}

	@Override
	public void setErrorHandler(ResponseErrorHandler handler) {
		interopClient.setErrorHandler(handler);
	}

	@Override
	public List<ApplicationSnapshot> listFiles() {
		return interopClient.listFiles();
	}
	
	@Override
	public ApplicationSnapshotInfo getSnapshotDetails(String name) {
		return interopClient.getSnapshotDetails(name);
	}

	@Override
	public void LcmExport() {
		throw new UnsupportedOperationException("Operation not supported yet");
	}


	@Override
	public File downloadFileViaStream(String filename) throws PbcsClientException {
		return interopClient.downloadFileViaStream(filename);
	}

	@Override
	public File downloadFileViaStream(String filename, String localFilename) {
		return interopClient.downloadFileViaStream(filename, localFilename);
	}

//	@Override
//	public MaintenanceWindow getMaintenanceWindow() {
//		return interopClient.getMaintenanceWindow();
//	}

}
