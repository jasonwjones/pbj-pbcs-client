package com.jasonwjones.pbcs.test;

import java.util.List;

import com.jasonwjones.pbcs.client.PbcsAppDimension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsApplication;

public class TestGetDimensions extends AbstractIntegrationTest {

	private static final Logger logger = LoggerFactory.getLogger(TestGetDimensions.class);

	public static void main(String[] args) {
		PbcsClient client = new PbcsClientFactory().createClient(connection);

		List<PbcsApplication> applications = client.getApplications();
		logger.info("Applications: {}", applications);

		for (PbcsApplication application : applications) {
			logger.info("Dims in application: {}", application.getName());
			printDims(application.getDimensions());
		}

//		logger.info("Scenario dimension");
//		printDims(Collections.singletonList(app.getDimension("Scenario")));

	}

	public static void printDims(List<PbcsAppDimension> dimensions) {
		for (PbcsAppDimension dimension : dimensions) {
			logger.info("Dimension: {}, valid in: {}", dimension.getName(), dimension.getValidPlans());
		}
	}

}