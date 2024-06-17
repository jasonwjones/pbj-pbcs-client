package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsJobStatus;
import com.jasonwjones.pbcs.client.PbcsPlanningClient;

public class TestRefreshCube extends AbstractIntegrationTest {

	public static void main(String[] args) throws InterruptedException {
		PbcsPlanningClient client = new PbcsClientFactory().createClient(connection);
		PbcsApplication app = client.getApplication("Vision");
		PbcsJobStatus result = app.refreshCube();
		System.out.println("Status: " + result.getDescriptiveStatus());
		result.waitUntilFinished();
	}

}