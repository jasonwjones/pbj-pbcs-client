package com.jasonwjones.pbcs.test;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;

public class TestInteropDownloadFile extends AbstractIntegrationTest {

	private static final Logger logger = LoggerFactory.getLogger(TestInteropDownloadFile.class);
	
	public static void main(String[] args) {
		PbcsClient client = new PbcsClientFactory().createClient(connection);
		//InteropClientImpl client = new InteropClientImpl(server, identityDomain, username, password);
		//client.getApiVersions();
		//client.downloadFile("ForecastData.txt");
		
		logger.info("Downloading file");
		//File export = client.downloadFile("ExportProduct.zip");
		File export = client.downloadFile("ForecastData.txt");
		
		//client.listFiles();
		
		//client.downloadFile("doesntexist.csv");

		logger.info("File count: {}", client.listFiles().size());
		//client.downloadFile("test.csv");
		//client.deleteFile("test.csv");
		//logger.info("File count: {}", client.listFiles().size());
		
//		client.listServices();
		
		
//		PbcsClient client = new PbcsClientImpl(server, identityDomain, username, password);
//		PbcsApplication app = client.getApplication(appName);
//		app.launchBusinessRule("AggAll");

//		PbcsClient client = new PbcsClientImpl(server, identityDomain, username, password);
//		PbcsApplication app = client.getApplication(appName);
//		app.launchBusinessRule("AggAll");
	}
	
}
