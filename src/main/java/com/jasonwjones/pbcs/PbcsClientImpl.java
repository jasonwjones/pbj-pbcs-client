package com.jasonwjones.pbcs;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.util.MultiValueMap;

import com.jasonwjones.pbcs.api.v3.JobLaunchResponse;
import com.jasonwjones.pbcs.api.v3.RestoreBackupResponse;
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

public class PbcsClientImpl implements PbcsClient {

	private final PbcsPlanningClient planningClient;

	private final InteropClient interopClient;

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
	public String getServer() {
		return planningClient.getServer();
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
	public File downloadFile(String filename, String localFilename) throws PbcsClientException {
		return interopClient.downloadFile(filename, localFilename);
	}

	@Override
	public String uploadFile(String filename) {
		return interopClient.uploadFile(filename);
	}

	@Override
	public String deleteFile(String filename) {
		return interopClient.deleteFile(filename);
	}

	@Override
	public JobLaunchResponse runRoleAssignmentReport(MultiValueMap<String, String> map) {
		return interopClient.runRoleAssignmentReport(map);
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
	public void LcmImport() {
		throw new UnsupportedOperationException("Operation not supported yet");
	}

	@Override
	public List<String> backupsList() {
		return interopClient.backupsList();
	}

	@Override
	public RestoreBackupResponse launchRestoreBackup(String backupName, Map<String, String> parameters) {
		return interopClient.launchRestoreBackup(backupName, parameters);
	}

	@Override
	public File downloadFileViaStream(String filename) throws PbcsClientException {
		return interopClient.downloadFileViaStream(filename);
	}

	@Override
	public File downloadFileViaStream(String filename, String localFilename) {
		return interopClient.downloadFileViaStream(filename, localFilename);
	}

	@Override
	public String uploadFile(String filename, Optional<String> remoteDir) {
		return interopClient.uploadFile(filename, remoteDir);
	}

}