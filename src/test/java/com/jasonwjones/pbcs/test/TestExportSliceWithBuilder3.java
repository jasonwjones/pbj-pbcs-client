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

public class TestExportSliceWithBuilder3 extends AbstractIntegrationTest {

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
		
		// can't roll with defaults for:
		// Scenario
		// Version
		// Analytics
		// Region
		
		GridDefinition grid2 = new GridDefinitionBuilder()
				//.pov(Arrays.asList("Account", "Analytics", "Business_Unit", "Currency", "Customer", "Install_Country", "Product", "Project", "Region", "Scenario", "Version"))
				//.pov(Arrays.asList("AC_F990000000", "BU_7RENEW", "Products_Total", "Region", "Customers_Total", "No Function", "MTD", "Actual", "Projects_Total", "AY_Input", "USD", "IC_AMERICAS", "PC_Renewables","Final" ))
				.pov(Arrays.asList("BU_7RENEW", "AC_F990000000", "Products_Total", "No Region", "Customers_Total", "No Function", "MTD", "Actual", "Projects_Total", "AY_Input", "USD", "IC_AMERICAS", "PC_Renewables","Final" ))
				.left("Sep", "Oct")
				.top("FY19", "FY18")
				.build();
		test(grid2);

	}
	
	public static DataSlice test(GridDefinition gridDefinition) {
		ExportDataSlice eds = new ExportDataSlice(gridDefinition);
		DataSlice slice = application.exportDataSlice("REFRPTG", eds);
		slicePrinter.print(slice);
		return slice;
	}
	
}
