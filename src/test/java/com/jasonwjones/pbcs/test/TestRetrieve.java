package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.client.impl.grid.DataSliceGrid;

import java.util.Arrays;
import java.util.List;

public class TestRetrieve extends AbstractIntegrationTest {

	public static void main(String[] args) {
		PbcsClient client = new PbcsClientFactory().createClient(connection);
		PbcsApplication app = client.getApplication("Vision");

		List<String> dims = Arrays.asList("Account", "Currency", "Entity", "Period", "Scenario", "Version", "Years");
		PbcsPlanType cube = app.getPlanType("Plan1", true, dims);

		DataSliceGrid grid = cube.retrieve(Arrays.asList("Actual", "Final", "Test1a", "FY21", "Account1", "USD", "IDescendants(Period)"));
		//IDescendants(
		grid.print();
	}

}