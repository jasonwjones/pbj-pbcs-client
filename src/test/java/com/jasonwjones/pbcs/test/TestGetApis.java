package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.client.PbcsPlanningClient;
import com.jasonwjones.pbcs.client.impl.PbcsPlanningClientImpl;

public class TestGetApis extends AbstractIntegrationTest {

	public static void main(String[] args) {
//		PbcsContext client = new PbcsContext(server, identityDomain, username, password);
//		List<PbcsApi> apis = client.getApis();
		
		PbcsPlanningClient client = new PbcsPlanningClientImpl(server, identityDomain, username, password);
		System.out.println("API: " + client.getApi());

		
//		PbcsClient client = new PbcsClientImpl(server, identityDomain, username, password);
//		PbcsApplication app = client.getApplication(appName);
//		app.launchBusinessRule("AggAll");
	}
	
}
