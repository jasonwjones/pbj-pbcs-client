package com.jasonwjones.pbcs.test;

import java.util.Arrays;
import java.util.List;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.api.v3.dataslices.DataSlice;
import com.jasonwjones.pbcs.api.v3.dataslices.DimensionMembers;
import com.jasonwjones.pbcs.api.v3.dataslices.ExportDataSlice;
import com.jasonwjones.pbcs.api.v3.dataslices.GridDefinition;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.util.SlicePrinter;

public class TestExportSlice extends AbstractIntegrationTest {

	public static void main(String[] args) throws Exception {
		PbcsClient client = new PbcsClientFactory().createClient(connection);
		PbcsApplication app = client.getApplication(appName);
		
		//DimensionMembers pov = new DimensionMembers(Arrays.asList("HSP_View", "Year", "Version", "Entity", "Product"), Arrays.asList("BaseData" , "FY15" , "Working", "410", "P_160"));
		//DimensionMembers pov = new DimensionMembers(Arrays.asList("HSP_View", "Year", "Version", "Entity", "Product"), Arrays.asList("BaseData" , "FY15" , "Working", "410", "P_160"));
		DimensionMembers pov = new DimensionMembers(Arrays.asList("HSP_View", "Years", "Version", "Entity", "Department"), Arrays.asList("BaseData" , "FY15" , "Working", "10010", "2079"));
		DimensionMembers columns = new DimensionMembers(Arrays.asList("Period"), Arrays.asList("IDescendants(YearTotal)"));
		
		//DimensionMembers columns2 = new DimensionMembers(Arrays.asList("Period"), Arrays.asList("IDescendants(Dec)"));
		
		List<String> period = Arrays.asList("Jan");
		List<String> scenario = Arrays.asList("Children(Scenario)");
		List<List<String>> test = Arrays.asList(period, scenario);
		
		DimensionMembers twoColumns = DimensionMembers.of(Arrays.asList("Period", "Scenario"), test);
		
		List<DimensionMembers> allCols = Arrays.asList(twoColumns);
		//List<DimensionMembers> allCols = Arrays.asList(columns);
		//List<DimensionMembers> allCols = Arrays.asList(columns, columns2);
		
		DimensionMembers rows = new DimensionMembers(Arrays.asList("Account"), Arrays.asList("30000.000"));
		List<DimensionMembers> allRows = Arrays.asList(rows);
		
		GridDefinition gridDefinition = new GridDefinition();
		gridDefinition.setSuppressMissingBlocks(false);
		gridDefinition.setPov(pov);
		gridDefinition.setColumns(allCols);
		gridDefinition.setRows(allRows);
		
		ExportDataSlice eds = new ExportDataSlice(gridDefinition);
		eds.setExportPlanningData(true);
		
		DataSlice slice = app.exportDataSlice("PlanPL", eds);
		
		SlicePrinter p = new SlicePrinter();
		p.print(slice);
	}

}
