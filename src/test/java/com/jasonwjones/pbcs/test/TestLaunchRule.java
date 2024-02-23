package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsJobLaunchResult;

public class TestLaunchRule extends AbstractIntegrationTest {

	public static void main(String[] args) throws Exception {
		PbcsClient client = new PbcsClientFactory().createClient(connection);
		PbcsApplication app = client.getApplication(appName);
		try {
			PbcsJobLaunchResult result = app.launchBusinessRule("calcall");
			result.waitUntilFinished();
		} catch (Exception e) {
			System.err.println("Couldn't launch rule: " + e.getMessage());
			throw e;
		}
	}

}