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
		logger.info("Downloading file");
		logger.info("File count: {}", client.listFiles().size());
		
		File downloaded = client.downloadFile("vision data 17-03-07");
		//File downloaded = client.downloadFile("apr/2017-02-28 08_31_41");
		
		
		//apr/2017-02-28 08_31_41
	}
	
}
