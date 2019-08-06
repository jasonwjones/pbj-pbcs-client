package com.jasonwjones.pbcs.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.api.v3.dataslices.DataSlice;
import com.jasonwjones.pbcs.api.v3.dataslices.ExportDataSlice;
import com.jasonwjones.pbcs.api.v3.dataslices.GridDefinition;
import com.jasonwjones.pbcs.api.v3.dataslices.GridDefinitionBuilder;
import com.jasonwjones.pbcs.api.v3.dataslices.DataSlice.HeaderDataRow;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.util.SlicePrinter;

public class TestExportSliceWithBuilder extends AbstractIntegrationTest {

	private static PbcsApplication application;
	
	private static SlicePrinter slicePrinter;
	
	public static void main(String[] args) throws Exception {
		PbcsClient client = new PbcsClientFactory().createClient(connection);
		application = client.getApplication(appName);
		slicePrinter = new SlicePrinter();
		
		List<String> dims = Arrays.asList("Account", "Analysis", "Company", "Comparison", "Department", "Entity", "Period", "Version", "Scenario", "Years");
		
		GridDefinition grid1 = new GridDefinitionBuilder()
				.auto(dims, "Years", "Scenario")
				.build();
		test(grid1);

		
		GridDefinition grid2 = new GridDefinitionBuilder()
				.pov(dims)
				.removePov("Scenario")
				.removePov("Company")
				.removePov("Years")
				.removePov("Period")
				.left("Scenario", "Company")
				.top("FY12", "Q1")
				.top("FY13", "Feb")
				.build();
		test(grid2);

		
		GridDefinition grid3 = new GridDefinitionBuilder()
				.pov(dims)
				.removePov("Scenario")
				.removePov("Company")
				//.removePov("Account")
				.removePov("Period")
				.removePov("Years")
				.left("Scenario", "Company")
				.top("Q1")
				//.top("Feb")
				.pivot()
				//.leftAdd("Account")
				.leftAdd("FY18", "FY19")
				//.leftAdd("Children(Years)")
				.build();
		test(grid3);
		
		
		GridDefinition grid4 = new GridDefinitionBuilder()
				.pov(dims)
				.removePov("Scenario", "Company", "Period", "Years")
				.top("Scenario", "Company")
				.leftAddToFirst("IChildren(Q1)", "IChildren(Q2)")
				.leftAdd("FY18", "FY19")
				.build();
		test(grid4);
		
		
		List<String> r1 = Arrays.asList("FY18", "Jan");
		List<String> r2 = Arrays.asList("FY18", "Feb");
		List<List<String>> top = Arrays.asList(r1, r2);
		
		List<String> l2 = Arrays.asList("Actual");
		List<String> l3 = Arrays.asList("Budget");
		List<List<String>> left = Arrays.asList(l2, l3);
		
		GridDefinition grid5 = new GridDefinitionBuilder()
				.pov(dims)
				.removePov("Scenario", "Period", "Years")
				.leftWithLists(left)
				.topWithLists(top)
				.build();
		DataSlice ds5 = test(grid5);
		//DataSliceTransformer.
	}
	
	public static DataSlice test(GridDefinition gridDefinition) {
		ExportDataSlice eds = new ExportDataSlice(gridDefinition);
		DataSlice slice = application.exportDataSlice("PlanPL", eds);
		slicePrinter.print(slice);
		return slice;
	}
	
}
