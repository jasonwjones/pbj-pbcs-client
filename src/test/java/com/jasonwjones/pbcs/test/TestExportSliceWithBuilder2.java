package com.jasonwjones.pbcs.test;

import java.util.Arrays;
import java.util.List;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.api.v3.dataslices.DataSlice;
import com.jasonwjones.pbcs.api.v3.dataslices.ExportDataSlice;
import com.jasonwjones.pbcs.api.v3.dataslices.GridDefinition;
import com.jasonwjones.pbcs.api.v3.dataslices.GridDefinitionBuilder;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.util.SlicePrinter;

public class TestExportSliceWithBuilder2 extends AbstractIntegrationTest {

	private static PbcsApplication application;
	
	private static SlicePrinter slicePrinter;
	
	public static void main(String[] args) throws Exception {
		
		PbcsClient client = new PbcsClientFactory().createClient(connection);
		application = client.getApplication(appName);
		slicePrinter = new SlicePrinter();
		
		//List<String> dims = Arrays.asList("Account", "Analysis", "Company", "Comparison", "Department", "Entity", "Version", "Scenario", "FY19");
		// period
		// Years: FY19
		// Analysis: Base
		// Company: 240
		// Comparison: ComparisonData
		// Department: 2400
		// Entity: 
		
		GridDefinition grid2 = new GridDefinitionBuilder()
				.pov(Arrays.asList("Base Salary", "Base", "240", "ComparisonData", "2400", "24000", "Working", "Budget", "FY19"))
				.left("Existing Employees")
				.top("YearTotal")
				.build();
		test(grid2);

	}
	
	public static DataSlice test(GridDefinition gridDefinition) {
		ExportDataSlice eds = new ExportDataSlice(gridDefinition);
		DataSlice slice = application.exportDataSlice("PlanEmp", eds);
		slicePrinter.print(slice);
		return slice;
	}
	
}
