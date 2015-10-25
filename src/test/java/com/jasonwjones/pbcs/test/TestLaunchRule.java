package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsClient;
import com.jasonwjones.pbcs.client.impl.PbcsClientImpl;

public class TestLaunchRule extends AbstractIntegrationTest {

	public static void main(String[] args) {
		PbcsClient client = new PbcsClientImpl(server, identityDomain, username, password);
		PbcsApplication app = client.getApplication(appName);
		app.launchBusinessRule("AggAll");
	}
	
}
