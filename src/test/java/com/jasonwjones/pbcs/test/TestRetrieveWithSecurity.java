package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsConnection;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.client.PbcsPlanningClient;
import com.jasonwjones.pbcs.client.impl.PlanTypeConfigurationImpl;
import com.jasonwjones.pbcs.client.impl.grid.DataSliceGrid;
import com.jasonwjones.pbcs.util.ConnectionUtils;

import java.util.Arrays;
import java.util.List;

public class TestRetrieveWithSecurity {

	public static void main(String[] args) {
		PbcsConnection connection = ConnectionUtils.defaultConnection();
		PbcsPlanningClient client = new PbcsClientFactory().createClient(connection);
		PbcsApplication app = client.getApplication("Vision");

		List<String> dims = Arrays.asList("Account", "Currency", "Entity", "Period", "Product", "Scenario", "Version", "Year");
		PbcsApplication.PlanTypeConfiguration configuration = new PlanTypeConfigurationImpl.Builder("Plan1")
				.skipCheck()
				.dimensions(dims)
				.build();

		PbcsPlanType cube = app.getPlanType(configuration);

		DataSliceGrid grid = cube.retrieve();

		grid.print();
	}

}