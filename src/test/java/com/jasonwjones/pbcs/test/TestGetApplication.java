package com.jasonwjones.pbcs.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsApplication;

public class TestGetApplication extends AbstractIntegrationTest {

	private static final Logger logger = LoggerFactory.getLogger(TestGetApplication.class);
	
	public static void main(String[] args) {
		PbcsClient client = new PbcsClientFactory().createClient(connection);
		
		logger.info("Applications: {}", client.getApplications());
		
		PbcsApplication app = client
				.getApplication(appName); 
		
	}	
	
}
