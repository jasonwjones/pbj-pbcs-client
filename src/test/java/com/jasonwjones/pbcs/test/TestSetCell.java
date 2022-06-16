package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsPlanType;

import java.util.Arrays;
import java.util.List;

public class TestSetCell extends AbstractIntegrationTest {

	public static void main(String[] args) {
		PbcsClient client = new PbcsClientFactory().createClient(connection);
		PbcsApplication app = client.getApplication("Vision");

		List<String> dims = Arrays.asList("Account", "Currency", "Entity", "Period", "Scenario", "Version", "Years");
		PbcsPlanType cube = app.getPlanType("Plan1", true, dims);

		List<String> pov = Arrays.asList("Actual", "FY21", "Final", "USD", "000", "P_000", "Jan", "4110");
		System.out.println("Get cell: " + cube.getCell(pov));

		cube.setCell(pov, "1000");
		System.out.println("Get cell after set: " + cube.getCell(pov));
	}

}