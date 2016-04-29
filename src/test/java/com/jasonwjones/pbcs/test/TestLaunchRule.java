package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsPlanningClient;
import com.jasonwjones.pbcs.client.impl.PbcsPlanningClientImpl;
import com.jasonwjones.pbcs.interop.impl.InteropClientImpl;

public class TestLaunchRule extends AbstractIntegrationTest {

	public static void main(String[] args) {
		PbcsPlanningClient client = new PbcsPlanningClientImpl(server, identityDomain, username, password);
		PbcsApplication app = client.getApplication(appName);
		app.launchBusinessRule("AggAll");
	}
	
}
