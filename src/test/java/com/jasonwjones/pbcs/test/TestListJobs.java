package com.jasonwjones.pbcs.test;

import java.util.List;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsJobDefinition;

public class TestListJobs extends AbstractIntegrationTest {

	public static void main(String[] args) {
		String template = "%-30s %-20s%n";
				
		PbcsClient client = new PbcsClientFactory().createClient(connection);

		
		List<PbcsApplication> apps = client.getApplications();
		for (PbcsApplication app : apps) {
			System.out.println("App: " + app.getName());
		}
		
		PbcsApplication app = client.getApplication(appName);
		
		List<PbcsJobDefinition> jobDefinitions = app.getJobDefinitions();
				
		System.out.printf(template, "Job Type", "Job Name");
		System.out.printf(template, "------------------------------", "--------------------");
		
		for (PbcsJobDefinition job : jobDefinitions) {
			System.out.printf(template, job.getJobType(), job.getJobName());
		}

	}
	
}
