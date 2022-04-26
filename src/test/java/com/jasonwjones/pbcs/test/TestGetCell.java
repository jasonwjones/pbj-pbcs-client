package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsPlanType;

import java.util.Arrays;
import java.util.List;

public class TestGetCell extends AbstractIntegrationTest {

	public static void main(String[] args) {
		PbcsClient client = new PbcsClientFactory().createClient(connection);
		PbcsApplication app = client.getApplication("Vision");

		List<String> dims = Arrays.asList("Account", "Currency", "Entity", "Period", "Scenario", "Version", "Years");
		PbcsPlanType cube = app.getPlanType("Plan1", true, dims);

		System.out.println("Get cell: " + cube.getCell(dims));
		System.out.println("Get cell: " + cube.getCell(Arrays.asList("Actual", "Final", "Test1a", "Jan", "FY21", "Account1", "USD")));
	}

}