package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsJobStatus;

public class TestRefreshCube extends AbstractIntegrationTest {

	public static void main(String[] args) throws InterruptedException {
		PbcsClient client = new PbcsClientFactory().createClient(connection);
		PbcsApplication app = client.getApplication("Vision");
		PbcsJobStatus result = app.refreshCube();
		System.out.println("Status: " + result.getDescriptiveStatus());
		result.waitUntilFinished();
	}

}