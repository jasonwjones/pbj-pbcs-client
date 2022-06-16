package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.Grid;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.client.impl.HashMapGrid;

import java.util.Arrays;
import java.util.List;

public class TestSetCells extends AbstractIntegrationTest {

	public static void main(String[] args) {
		PbcsClient client = new PbcsClientFactory().createClient(connection);
		PbcsApplication app = client.getApplication("Vision");

		List<String> dims = Arrays.asList("Account", "Currency", "Entity", "Period", "Scenario", "Version", "Years");
		PbcsPlanType cube = app.getPlanType("Plan1", true, dims);

		List<String> pov = Arrays.asList("Actual", "Final", "USD", "000", "P_000", "4110");

		Grid<String> grid = new HashMapGrid<>(3, 2);
		grid.setCell(0, 1, "FY21");
		grid.setCell(1, 0, "Jan");
		grid.setCell(2, 0, "Feb");
		grid.setCell(1, 1, "100");
		grid.setCell(2, 1, "200");

		//System.out.println("Get cell: " + cube.getCell(pov));

		cube.setCells(pov, grid);
		//System.out.println("Get cell after set: " + cube.getCell(pov));
	}

}