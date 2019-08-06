package com.jasonwjones.pbcs.test;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsDimension;

public class TestGetDimensions extends AbstractIntegrationTest {

	private static final Logger logger = LoggerFactory.getLogger(TestGetDimensions.class);
	
	public static void main(String[] args) {
		PbcsClient client = new PbcsClientFactory().createClient(connection);
		
		logger.info("Applications: {}", client.getApplications());
		
		PbcsApplication app = client.getApplication(appName);
		
		logger.info("All dims:");
		printDims(app.getDimensions());
		
		logger.info("Dims in PlanPL:");
		printDims(app.getDimensions("PlanPL"));
		
		logger.info("Scenario dimension");
		printDims(Collections.singletonList(app.getDimension("Scenario")));
		
	}	
	
	public static void printDims(List<PbcsDimension> dimensions) {
		for (PbcsDimension dimension : dimensions) {
			logger.info("Dimension: {}, valid in: {}", dimension.getName(), dimension.getValidPlans());
		}
	}
	
}
