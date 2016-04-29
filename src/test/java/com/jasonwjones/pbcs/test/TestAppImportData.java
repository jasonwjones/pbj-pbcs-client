package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.client.PbcsPlanningClient;
import com.jasonwjones.pbcs.client.impl.PbcsPlanningClientImpl;

public class TestAppImportData extends AbstractIntegrationTest {

	public static void main(String[] args) {
		PbcsPlanningClient client = new PbcsPlanningClientImpl(server, identityDomain, username, password); 
		client.getApplication(appName).launchDataImport("ForecastData");		
	}
		
}
