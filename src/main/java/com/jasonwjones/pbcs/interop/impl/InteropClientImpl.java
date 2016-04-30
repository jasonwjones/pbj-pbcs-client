package com.jasonwjones.pbcs.interop.impl;

import java.io.File;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.jasonwjones.pbcs.api.v3.HypermediaLink;
import com.jasonwjones.pbcs.api.v3.ServiceDefinitionWrapper;
import com.jasonwjones.pbcs.client.PbcsConnection;
import com.jasonwjones.pbcs.client.PbcsServiceConfiguration;
import com.jasonwjones.pbcs.interop.InteropClient;

public class InteropClientImpl implements InteropClient {

	private static final Logger logger = LoggerFactory.getLogger(InteropClientImpl.class);
		
	private RestTemplate restTemplate;

	private String baseUrl;
		
	private PbcsServiceConfiguration serviceConfiguration;
	
	public InteropClientImpl(PbcsConnection connection, PbcsServiceConfiguration serviceConfiguration) {
		logger.info("Initializing PBCS Interop API");
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
	
	public ApplicationSnapshotInfo getFileInfo(String filename) {
		return null;
	}
	
	@Override
	public void uploadFile(String filename) {
		byte[] data = new byte[]{'a'};
		
		//File uploadFile = new File(filename);
		String url = String.format("/applicationsnapshots/%s/contents?q={chunkSize:%d,isFirst:%b,isLast:%b}", filename, data.length, true, true);

		URI uri = UriComponentsBuilder.fromHttpUrl(this.baseUrl + serviceConfiguration.getInteropApiVersion() + url).build().toUri();
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<byte[]> entity = new HttpEntity<byte[]>(new byte[]{'a'}, headers);
		headers.set("Content-Type", "application/octet-stream");
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
		logger.info("Headers (upload): " + response.getHeaders());
		System.out.println("Code: " + response.getStatusCode().value());
		System.out.println("Response: " + response.getBody());
	}
	
	@Override
	public File downloadFile(String filename) {
		logger.info("Requested file download: {}", filename);
		// TODO Auto-generated method stub
		
		String url = this.baseUrl + serviceConfiguration.getInteropApiVersion() + String.format("/applicationsnapshots/%s/contents", filename);
		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
		
		logger.info("Headers: " + response.getHeaders());
		
		// likely to be zip or csv (can it be xml?)
		String extension = response.getHeaders().get("fileExtension").get(0);
		
		logger.info("Canonical file extension for local file: {}", extension); 
		
		System.out.println("Content-Type: " + response.getHeaders().getContentType());
		System.out.println("Is JSON: " + response.getHeaders().getContentType().isCompatibleWith(MediaType.APPLICATION_JSON));
		System.out.println("Code: " + response.getStatusCode().value());
		System.out.println("Response: " + response.getBody());

		return null;
	}

	// TODO: apparently PBCS REST API returns a JSON payload so we might need to 
	// switch to using the exchange() method to get the details
	public void deleteFile(String filename) {
		logger.info("Deleting {}", filename);
		restTemplate.delete(baseUrl + serviceConfiguration.getInteropApiVersion() + "/applicationsnapshots/{filename}", filename);
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
	public void LcmExport() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void LcmImport() {
		// TODO Auto-generated method stub
		
	}
	
}
