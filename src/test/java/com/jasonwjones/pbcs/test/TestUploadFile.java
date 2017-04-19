package com.jasonwjones.pbcs.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;

public class TestUploadFile extends AbstractIntegrationTest {

	private static final Logger logger = LoggerFactory.getLogger(TestUploadFile.class);
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		PbcsClient client = new PbcsClientFactory().createClient(connection);
		//InteropClientImpl client = new InteropClientImpl(server, identityDomain, username, password);
		//client.getApiVersions();
		//client.downloadFile("ForecastData.txt");
		
		client.deleteFile("test.csv");
		logger.info("File count: {}", client.listFiles().size());
		client.uploadFile("test.csv");
		//client.listFiles();
		
		//client.downloadFile("doesntexist.csv");

		logger.info("File count: {}", client.listFiles().size());
		File downloaded = client.downloadFile("test.csv");
		
        BufferedReader b = new BufferedReader(new FileReader(downloaded));
        String readLine = "";
        System.out.println("Reading file using Buffered Reader");

        while ((readLine = b.readLine()) != null) {
            System.out.println(readLine);
        }
		b.close();
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
