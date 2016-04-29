package com.jasonwjones.pbcs.interop.impl;

import java.io.File;
import java.net.URI;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
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
import com.jasonwjones.pbcs.client.exceptions.PbcsClientException;
import com.jasonwjones.pbcs.client.impl.AuthHttpComponentsClientHttpRequestFactory;
import com.jasonwjones.pbcs.client.impl.RestContext;
import com.jasonwjones.pbcs.interop.InteropClient;

public class InteropClientImpl implements InteropClient {

	private static final Logger logger = LoggerFactory.getLogger(InteropClientImpl.class);
	
	private RestContext context;
	
	private RestTemplate restTemplate;

	private String baseUrl;
	
	//private PbcsApi api;
	
	// URL(String.format("%s/interop/rest/%s/applicationsnapshots/%s/contents?q={chunkSize:%d,isFirst:%b,isLast:%b}", serverUrl, apiVersion, fileName, lastChunk.length, isFirst, isLast));
	//private static String PATH = "/HyperionPlanning/rest/";
	private static String PATH = "/interop/rest/";
	
	private static String SCHEME = "https";
	
	private static int PORT = 443;
	
	private static String defaultVersion = "11.1.2.3.600";
	
	public InteropClientImpl(String server, String identityDomain, String username, String password) throws PbcsClientException {
		logger.info("Initializing PBCS API");
		HttpClient httpClient = HttpClients.createDefault();

		final HttpHost httpHost = new HttpHost(server, PORT, SCHEME);
		final String fullUsername = identityDomain + "." + username;
		final AuthHttpComponentsClientHttpRequestFactory requestFactory = new AuthHttpComponentsClientHttpRequestFactory(
				httpClient, httpHost, fullUsername, password);
		
		this.baseUrl = SCHEME + "://" + server + PATH; // + defaultVersion; // + "/" + "applicationsnapshots";
		this.restTemplate = new RestTemplate(requestFactory);
		this.context = new RestContext(restTemplate, baseUrl);
		
		// post to base url brings back a JSON object with *links* such as to 
		// applicationsnapshots and others
		ResponseEntity<String> checkApi = restTemplate.getForEntity(baseUrl + defaultVersion, String.class);
		System.out.println("Content: " + checkApi.getBody());
		
		logger.info("Initialized");
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
		ResponseEntity<ApplicationSnapshotsWrapper> snaps = restTemplate.getForEntity(baseUrl + defaultVersion + "/applicationsnapshots", ApplicationSnapshotsWrapper.class);
		return snaps.getBody().getItems();
	}
	
	public ApplicationSnapshotInfo getFileInfo(String filename) {
		return null;
	}
	
	@Override
	public void uploadFile(String filename) {
		byte[] data = new byte[]{'a'};
		
		//File uploadFile = new File(filename);
		String url = String.format("/applicationsnapshots/%s/contents?q={chunkSize:%d,isFirst:%b,isLast:%b}", filename, data.length, true, true);

		URI uri = UriComponentsBuilder.fromHttpUrl(this.baseUrl + defaultVersion + url).build().toUri();
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<byte[]> entity = new HttpEntity<byte[]>(new byte[]{'a'}, headers);
		headers.set("Content-Type", "application/octet-stream");
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
		System.out.println("Code: " + response.getStatusCode().value());
		System.out.println("Response: " + response.getBody());
	}
	
	@Override
	public File downloadFile(String filename) {
		logger.info("Requested file download: {}", filename);
		// TODO Auto-generated method stub
		
		String url = this.baseUrl + defaultVersion + String.format("/applicationsnapshots/%s/contents", filename);
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
		restTemplate.delete(baseUrl + defaultVersion + "/applicationsnapshots/{filename}", filename);
	}
	
	public void listServices() {
		logger.info("Listing services");
		ResponseEntity<ServiceDefinitionWrapper> response = restTemplate.getForEntity(baseUrl + defaultVersion + "/services", ServiceDefinitionWrapper.class);
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
