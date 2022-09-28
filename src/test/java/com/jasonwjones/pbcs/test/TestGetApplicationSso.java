package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsDimension;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TestGetApplicationSso extends AbstractIntegrationTest {

	private static final Logger logger = LoggerFactory.getLogger(TestGetApplicationSso.class);

	public static void main(String[] args) {
		PbcsClient client = new PbcsClientFactory().createClient(connection);

		System.out.println("Apps:");
		//logger.info("Applications: {}", client.getApplications());

		for (PbcsApplication app : client.getApplications()) {
			logger.info("App: {}", app);
			List<PbcsPlanType> planTypes = app.getPlanTypes();
			for (PbcsPlanType planType : planTypes) {
				System.out.println(" - plan: " + planType.getName());
			}

			for (PbcsDimension dimension : app.getDimensions()) {
				System.out.println(" - " + dimension.getName());
			}

		}


	}

}