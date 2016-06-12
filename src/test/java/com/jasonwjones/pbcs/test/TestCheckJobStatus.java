package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsJobStatus;
import com.jasonwjones.pbcs.client.exceptions.PbcsClientException;

public class TestCheckJobStatus extends AbstractIntegrationTest {

	public static void main(String[] args) {
		try {
			PbcsClient client = new PbcsClientFactory().createClient(connection);
			PbcsApplication app = client.getApplication(appName);
			//import resp: {"status":-1,"details":null,"jobId":95,"descriptiveStatus":"Processing","jobName":"ForecastData","links":[{"rel":"self","href":"https://pbcsloaner1svc-pbcsloaner1.pbcs.us2.oraclecloud.com/HyperionPlanning/rest/v3/applications/Vision/jobs/95","action":"GET"},{"rel":"job-details","href":"https://pbcsloaner1svc-pbcsloaner1.pbcs.us2.oraclecloud.com/HyperionPlanning/rest/v3/applications/Vision/jobs/95/details","action":"GET"}]}

			PbcsJobStatus jobStatus = app.getJobStatus(121);
			System.out.println("Job status: " + jobStatus);
		} catch (PbcsClientException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

}
