package com.jasonwjones.pbcs.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;

public class TestInteropClient extends AbstractIntegrationTest {

	private static final Logger logger = LoggerFactory.getLogger(TestInteropClient.class);
	
	public static void main(String[] args) {
		PbcsClient client = new PbcsClientFactory().createClient(connection);
		//InteropClientImpl client = new InteropClientImpl(server, identityDomain, username, password);
		//client.getApiVersions();
		//client.downloadFile("ForecastData.txt");
		
		client.uploadFile("test.csv");
		//client.listFiles();
		
		//client.downloadFile("doesntexist.csv");

		logger.info("File count: {}", client.listFiles().size());
		client.downloadFile("test.csv");
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
