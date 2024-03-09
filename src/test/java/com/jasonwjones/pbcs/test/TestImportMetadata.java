package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsJobDefinition;
import com.jasonwjones.pbcs.client.PbcsJobStatus;
import com.jasonwjones.pbcs.client.exceptions.PbcsClientException;

public class TestImportMetadata extends AbstractIntegrationTest {

	public static void main(String[] args) throws Exception {
		PbcsClient client = new PbcsClientFactory().createClient(connection);

		PbcsApplication app = client.getApplication("MDP_Demo");

		try {
			for (PbcsJobDefinition job : app.getJobDefinitions()) {
				System.out.println("Job: " + job.getName() + " type: " + job.getJobType());
			}
			PbcsJobStatus result = app.importMetadata("testimport");
			System.out.println("Result: "+ result);
		} catch (PbcsClientException e) {
			System.err.println("Error importing: " + e.getMessage());
		}

//		while (result.getStatus() == -1) {
//			Thread.sleep(2000);
//			PbcsJobStatus status = app.getJobStatus(result.getJobId());
//			System.out.println("Status: " + status);
//		}

	}

}