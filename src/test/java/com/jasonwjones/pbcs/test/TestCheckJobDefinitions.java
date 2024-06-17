package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsJobDefinition;
import com.jasonwjones.pbcs.client.PbcsPlanningClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestCheckJobDefinitions extends AbstractIntegrationTest {

	private static final Logger logger = LoggerFactory.getLogger(TestCheckJobDefinitions.class);

	public static void main(String[] args) throws Exception {
		PbcsPlanningClient client = new PbcsClientFactory().createClient(connection);
		PbcsApplication app = client.getApplication("Vision");

		for (PbcsJobDefinition jobDefinition : app.getJobDefinitions()) {
			logger.info("Job: {}", jobDefinition);
		}
	}

}