package com.jasonwjones.pbcs.test;

import java.util.List;

import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsPlanningClient;
import com.jasonwjones.pbcs.client.PbcsJobDefinition;
import com.jasonwjones.pbcs.client.impl.PbcsPlanningClientImpl;

public class TestListJobs extends AbstractIntegrationTest {

	public static void main(String[] args) {
		String template = "%-30s %-20s%n";
				
		PbcsPlanningClient client = new PbcsPlanningClientImpl(server, identityDomain, username, password);
		
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
