package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsPlanningClient;
import com.jasonwjones.pbcs.client.PbcsJobLaunchResult;
import com.jasonwjones.pbcs.client.PbcsJobStatus;
import com.jasonwjones.pbcs.client.impl.PbcsPlanningClientImpl;

public class TestExportData extends AbstractIntegrationTest {

	public static void main(String[] args) throws Exception {
		PbcsPlanningClient client = new PbcsPlanningClientImpl(server, identityDomain, username, password);
		PbcsApplication app = client.getApplication(appName);
		PbcsJobLaunchResult result = app.exportData("exportplantasticact01");
		
		System.out.println("Result: "+ result);
		
		while (result.getStatus() == -1) {
			Thread.sleep(2000);
			PbcsJobStatus status = app.getJobStatus(result.getJobId());
			System.out.println("Status: " + status);
		}
		
	}

}
