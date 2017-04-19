package com.jasonwjones.pbcs.test;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;

public class TestDeleteFile extends AbstractIntegrationTest {

	private static final Logger logger = LoggerFactory.getLogger(TestDeleteFile.class);
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		PbcsClient client = new PbcsClientFactory().createClient(connection);
		
		logger.info("File count: {}", client.listFiles().size());
		client.deleteFile("test.csv");
		logger.info("File count: {}", client.listFiles().size());
	}
	
}
