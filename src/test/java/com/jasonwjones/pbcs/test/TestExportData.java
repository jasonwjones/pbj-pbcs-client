package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsJobStatus;
import com.jasonwjones.pbcs.client.PbcsPlanningClient;

public class TestExportData extends AbstractIntegrationTest {

	public static void main(String[] args) throws Exception {
		PbcsPlanningClient client = new PbcsClientFactory().createClient(connection);

		PbcsApplication app = client.getApplication("Vision");
		PbcsJobStatus result = app.importMetadata("Plan1");

		System.out.println("Result: " + result);

		while (result.getStatus() == -1) {
			Thread.sleep(2000);
			PbcsJobStatus status = app.getJobStatus(result.getJobId());
			System.out.println("Status: " + status);
		}

	}

}