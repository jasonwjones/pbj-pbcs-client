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

		List<String> dims = Arrays.asList("Account", "Currency", "Entity", "Period", "Product", "Scenario", "Version", "Year");
		PbcsPlanType cube = app.getPlanType("Plan1", true, dims);

		System.out.println("Get cell: " + cube.getCell(dims));

		List<String> dims2 = Arrays.asList("4110", "USD", "000", "Jan", "P_000", "Actual", "Final", "FY22");
		System.out.println("Get cell: " + cube.getCell(dims2));
	}

}