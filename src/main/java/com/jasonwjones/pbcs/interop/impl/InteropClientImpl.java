package com.jasonwjones.pbcs.interop.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jasonwjones.pbcs.api.v3.HypermediaLink;
import com.jasonwjones.pbcs.api.v3.JobLaunchResponse;
import com.jasonwjones.pbcs.api.v3.MaintenanceWindow;
import com.jasonwjones.pbcs.api.v3.RestoreBackupPayload;
import com.jasonwjones.pbcs.api.v3.RestoreBackupResponse;
import com.jasonwjones.pbcs.api.v3.ServiceDefinitionWrapper;
import com.jasonwjones.pbcs.client.PbcsConnection;
import com.jasonwjones.pbcs.client.PbcsServiceConfiguration;
import com.jasonwjones.pbcs.client.exceptions.PbcsClientException;
import com.jasonwjones.pbcs.interop.InteropClient;

public class InteropClientImpl implements InteropClient {

	private static final Logger logger = LoggerFactory.getLogger(InteropClientImpl.class);
		
	private RestTemplate restTemplate;

	private String baseUrl;
		
	private PbcsServiceConfiguration serviceConfiguration;
	
	public InteropClientImpl(PbcsConnection connection, PbcsServiceConfiguration serviceConfiguration) {
		logger.debug("Initializing PBCS Interop API");
		this.serviceConfiguration = serviceConfiguration;
		this.baseUrl = serviceConfiguration.getScheme() + "://" + connection.getServer() + serviceConfiguration.getInteropRestApiPath(); // + defaultVersion; // + "/" + "applicationsnapshots";
		this.restTemplate = new RestTemplate(serviceConfiguration.createRequestFactory(connection));
		
		// post to base url brings back a JSON object with *links* such as to 
		// applicationsnapshots and others
		//ResponseEntity<String> checkApi = restTemplate.getForEntity(baseUrl + serviceConfiguration.getInteropApiVersion(), String.class);
		//System.out.println("Content: " + checkApi.getBody());
		
		//logger.info("Initialized");
//		ResponseEntity<String> snapshots = restTemplate.getForEntity(baseUrl + defaultVersion + "/applicationsnapshots", String.class);
//		System.out.println("Content: " + snapshots.getBody());
//
//		ResponseEntity<ApplicationSnapshotsWrapper> snaps = restTemplate.getForEntity(baseUrl+ defaultVersion + "/applicationsnapshots", ApplicationSnapshotsWrapper.class);
//		System.out.println("Snaps: " + snaps);
//
//		
//		// known types: LCM, EXTERNAL
//		String template = "%-20s %-30s %-20s %-20s%n";
//		System.out.printf(template, "Last Modified Time", "Name", "Type", "Size");
//		System.out.printf(template, "--------------------", "--------------------", "---", "---");
//
//		for (ApplicationSnapshot snapshot : snaps.getBody().getItems()) {
//			System.out.printf(template, snapshot.getLastModifiedTime(), snapshot.getName(), snapshot.getType(), snapshot.getSize());		
//		}
//		
		//Vision SS 14 Feb 2016
	}
	
	public void getApiVersions() {
		logger.info("Listing REST API versions");
		ResponseEntity<String> response = restTemplate.getForEntity(baseUrl, String.class);
		System.out.println("Respo: " + response.getBody());
	}
	
	public List<ApplicationSnapshot> listFiles() {
		ResponseEntity<ApplicationSnapshotsWrapper> snaps = restTemplate.getForEntity(baseUrl + serviceConfiguration.getInteropApiVersion() + "/applicationsnapshots", ApplicationSnapshotsWrapper.class);		
		return Collections.unmodifiableList(snaps.getBody().getItems());
	}
	
	public ApplicationSnapshotInfo getSnapshotDetails(String name) {
		//ResponseEntity<ApplicationSnapshotInfo> snaps = restTemplate.getForEntity(baseUrl + serviceConfiguration.getInteropApiVersion() + "/applicationsnapshots", ApplicationSnapshotsWrapper.class);
		//ResponseEntity<String> snap = restTemplate.getForEntity(baseUrl + serviceConfiguration.getInteropApiVersion() + "/applicationsnapshots/" + name, String.class);
		ResponseEntity<ApplicationSnapshotInfoWrapper> snap = restTemplate.getForEntity(baseUrl + serviceConfiguration.getInteropApiVersion() + "/applicationsnapshots/" + name, ApplicationSnapshotInfoWrapper.class);
		//restTemplate.delete(baseUrl + serviceConfiguration.getInteropApiVersion() + "/applicationsnapshots/" + name);
		System.out.println(snap.getBody());
		return null;
	}

	@Override
	public String uploadFile(String filename) {
		return uploadFile(filename, Optional.empty());
	}

	@Override
	public String uploadFile(String filename, Optional<String> remoteDir) {
		File fileToUpload = new File(filename);
		if (!fileToUpload.exists()) {
			logger.error("File {} does not exist", filename);
			throw new PbcsClientException("File to upload does not exist: " + filename);
		} else {
			logger.info("Found local file");
		}

		try {
			String filenameOnly = SimpleFilenameUtils.getName(filename);
			logger.info("Source file {} will be {} on target", filename, filenameOnly);

			final InputStream fis = new FileInputStream(filename); // or whatever
			final RequestCallback requestCallback = request -> {
				request.getHeaders()
					   .add("Content-type", "application/octet-stream");
				IOUtils.copy(fis, request.getBody());
			};
			final HttpMessageConverterExtractor<String> responseExtractor = new HttpMessageConverterExtractor<>(String.class,
																												restTemplate.getMessageConverters());
			String url = String.format("/applicationsnapshots/%s/contents?q={chunkSize:%d,isFirst:%b,isLast:%b" + (remoteDir.isPresent() ?
					",\"extDirPath\":\"" + remoteDir.get() + "\"}" :
					"}"), filenameOnly, fis.available(), true, true);

			URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl + serviceConfiguration.getInteropApiVersion() + url)
										  .build()
										  .toUri();
			return restTemplate.execute(uri, HttpMethod.POST, requestCallback, responseExtractor);
		} catch (IOException e) {
			throw new PbcsClientException("Couldn't read/upload file", e);
		}
	}
	
	// upload local txt to zip
	public void uploadFile(String remoteFilename, List<String> localFiles) {
		throw new RuntimeException("Not implemented yet");
	}
	
	@Override
	public File downloadFile(String filename) {
		return downloadFile(filename, filename);
	}
	
	// Should return a rich object:
	// File getFile()
	// extract(String targetDir)
	// etc.
	@Override
	public File downloadFile(String filename, String localFilename) {
		logger.info("Requested file download: {}", filename);
		// TODO Auto-generated method stub
		
		File localFile = new File(localFilename);
		if (localFile.isDirectory()) {
			logger.info("Will download {} to local folder {}", filename, localFile);
		} else {
			
		}
		
		String url = this.baseUrl + serviceConfiguration.getInteropApiVersion() + String.format("/applicationsnapshots/%s/contents", filename);
		//ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
		
		HttpHeaders headers = new HttpHeaders();
	    headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);
		
		logger.info("Headers: " + response.getHeaders());
		
		// likely to be zip or csv (can it be xml?)
		String extension = response.getHeaders().get("fileExtension").get(0);
		
		logger.info("Canonical file extension for local file: {}", extension); 
		
//		System.out.println("Content-Type: " + response.getHeaders().getContentType());
//		System.out.println("Is JSON: " + response.getHeaders().getContentType().isCompatibleWith(MediaType.APPLICATION_JSON));
//		System.out.println("Code: " + response.getStatusCode().value());
//		System.out.println("Response: " + response.getBody());

		try {
			File outputFile = new File(localFilename);
			// TODO: add file extension if the target file does not end with it
			FileUtils.writeByteArrayToFile(outputFile, response.getBody());
			return outputFile;
		} catch (IOException e) {
			logger.error("Unable to write local file", e);
			throw new PbcsClientException("Unable to write downloaded file", e);
		}
	}

	// switch to using the exchange() method to get the details
	public String deleteFile(String filename) {
		HttpHeaders headers = new HttpHeaders();
		filename = filename.replaceAll("/", "\\\\");
		headers.setContentType(MediaType.APPLICATION_JSON);
		ResponseEntity<String> exchange = restTemplate.exchange(baseUrl + serviceConfiguration.getInteropApiVersion() + "/applicationsnapshots/" + filename,
																HttpMethod.DELETE,
																new HttpEntity<Object>("", headers),
																String.class,
																new HashMap<>());
		return exchange.toString();
	}
	
	public void listServices() {
		logger.info("Listing services");
		ResponseEntity<ServiceDefinitionWrapper> response = restTemplate.getForEntity(baseUrl + serviceConfiguration.getInteropApiVersion() + "/services", ServiceDefinitionWrapper.class);
		System.out.println("Code: " + response.getStatusCode().value());
		System.out.println("Response: " + response.getBody());
		
		for (HypermediaLink link : response.getBody().getLinks()) {
			System.out.println(link);
		}
		
	}

	@Override
	public JobLaunchResponse runRoleAssignmentReport(MultiValueMap<String, String> map) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(map, headers);

		ResponseEntity<JobLaunchResponse> response = restTemplate.postForEntity(baseUrl + "security/v1/roleassignmentreport",
																				requestEntity,
																				JobLaunchResponse.class,
																				map);
		return response.getBody();
	}
	
	//@Override
	public MaintenanceWindow getMaintenanceWindow() {
		ResponseEntity<String> response  = restTemplate.getForEntity(baseUrl + serviceConfiguration.getInteropApiVersion() + "/dailymaintenance", String.class);
		System.out.println("Response: " + response.getBody());
		return null;
	}
	
	@Override
	public void LcmExport() {
		// TODO Auto-generated method stub

	}

	@Override
	public void LcmImport() {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> backupsList() {
		ResponseEntity<ServiceDefinitionWrapper> response = restTemplate.getForEntity(baseUrl + "v2/backups/list", ServiceDefinitionWrapper.class);
		ServiceDefinitionWrapper body = response.getBody();
		return body.getItems();
	}

	@Override
	public RestoreBackupResponse launchRestoreBackup(String backupName, Map<String, String> parameters) {
		String url = baseUrl + ("v2/backups/restore");
		RestoreBackupPayload payload = new RestoreBackupPayload(backupName);
		payload.setParameters(parameters);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		try {
			HttpEntity<Object> requestEntity = new HttpEntity<>(new ObjectMapper().writer()
																				  .withDefaultPrettyPrinter()
																				  .writeValueAsString(payload), headers);
			ResponseEntity<RestoreBackupResponse> output = restTemplate.postForEntity(url, requestEntity, RestoreBackupResponse.class);
			RestoreBackupResponse body = output.getBody();
			body = waitForCompletion(body);
			return body;
		} catch (Exception e) {
			throw new RuntimeException("Exception during launch restore backup request", e);
		}
	}

	private RestoreBackupResponse waitForCompletion(RestoreBackupResponse body) {
		String status = body.getStatus();
		while ("-1".equals(status)) {
			final Optional<HypermediaLink> jobStatusLink = body.getLinks()
															   .stream()
															   .filter(hypermediaLink -> hypermediaLink.getHyperlink()
																									   .contains("/status/jobs/"))
															   .findAny();
			if (jobStatusLink.isPresent()) {
				final ResponseEntity<RestoreBackupResponse> entity = restTemplate.getForEntity(jobStatusLink.get()
																											.getHyperlink(), RestoreBackupResponse.class);
				status = entity.getBody()
							   .getStatus();
				body = entity.getBody();
			}
		}
		return body;
	}

	private byte[] readFileToBytes(String filename) throws IOException {
		File fff = new File(filename);
		FileInputStream fileInputStream = new FileInputStream(fff);
		int byteLength = (int) fff.length(); //bytecount of the file-content
		byte[] filecontent = new byte[byteLength];
		fileInputStream.read(filecontent, 0, byteLength);
		fileInputStream.close();
		return filecontent;
	}

	@Override
	public File downloadFileViaStream(String filename) throws PbcsClientException {
		return downloadFileViaStream(filename, filename);
	}

	@Override
	public File downloadFileViaStream(String filename, String localFilename) {
		logger.info("Requested file download: {}", filename);
		File localFile = new File(localFilename);
		if (localFile.isDirectory()) {
			logger.info("Will download {} to local folder {}", filename, localFile);
		} else {

		}

		String url = this.baseUrl + serviceConfiguration.getInteropApiVersion()
				+ String.format("/applicationsnapshots/%s/contents", filename);

		final HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));
		
		try {
			final File outputFile = File.createTempFile(localFilename, "");
			restTemplate.execute(url, HttpMethod.GET, new RequestCallback() {
				@Override
				public void doWithRequest(ClientHttpRequest request) throws IOException {
					for (Entry<String, List<String>> header : headers.entrySet()) {
						request.getHeaders().put(header.getKey(), header.getValue());
					}

				}
			}, new ResponseExtractor<Void>() {

				@Override
				public Void extractData(ClientHttpResponse response) throws IOException {
					try (InputStream body = response.getBody()) {
						FileOutputStream output = new FileOutputStream(outputFile);
						IOUtils.copy(body, output);
						return null;
					}
				}
			});
			return outputFile;
		} catch (IOException e) {
			logger.error("Unable to write local file", e);
			throw new PbcsClientException("Unable to write downloaded file", e);
		}
	}
	
}
