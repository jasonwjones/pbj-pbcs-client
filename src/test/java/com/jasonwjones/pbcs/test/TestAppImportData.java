package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsPlanningClient;

public class TestAppImportData extends AbstractIntegrationTest {

	public static void main(String[] args) {
		PbcsPlanningClient client = new PbcsClientFactory().createClient(connection);
		client.getApplication(appName).launchDataImport("ForecastData");
	}

}