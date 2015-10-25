package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsClient;
import com.jasonwjones.pbcs.client.PbcsJobStatus;
import com.jasonwjones.pbcs.client.exceptions.PbcsClientException;
import com.jasonwjones.pbcs.client.impl.PbcsClientImpl;

public class TestCheckJobStatus extends AbstractIntegrationTest {

	public static void main(String[] args) {
		try {
			PbcsClient client = new PbcsClientImpl(server, identityDomain, username, password);
			PbcsApplication app = client.getApplication(appName);
			PbcsJobStatus jobStatus = app.getJobStatus(558);
			System.out.println("Job status: " + jobStatus);
		} catch (PbcsClientException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

}
