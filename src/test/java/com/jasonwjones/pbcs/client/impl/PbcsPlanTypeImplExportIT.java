package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.api.v3.dataslices.DataSlice;
import com.jasonwjones.pbcs.api.v3.dataslices.ExportDataSlice;
import com.jasonwjones.pbcs.api.v3.dataslices.GridDefinition;
import com.jasonwjones.pbcs.api.v3.dataslices.GridDefinitionBuilder;
import com.jasonwjones.pbcs.util.SlicePrinter;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class PbcsPlanTypeImplExportIT extends AbstractVisionCubeIT {

    private final SlicePrinter slicePrinter = new SlicePrinter();

    @Test
    public void whenExport1() {
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

        List<String> l2 = List.of("Actual");
        List<String> l3 = List.of("Budget");
        List<List<String>> left = Arrays.asList(l2, l3);

        GridDefinition grid5 = new GridDefinitionBuilder()
                .pov(dims)
                .removePov("Scenario", "Period", "Years")
                .leftWithLists(left)
                .topWithLists(top)
                .build();
        DataSlice ds5 = test(grid5);
    }

    @Test
    public void whenExport2() {
        GridDefinition grid2 = new GridDefinitionBuilder()
                .pov(Arrays.asList("Base Salary", "Base", "240", "ComparisonData", "2400", "24000", "Working", "Budget", "FY19"))
                .left("Existing Employees")
                .top("YearTotal")
                .build();
        test(grid2);
    }

    @Test
    public void whenExport3() {
        GridDefinition grid2 = new GridDefinitionBuilder()
                //.pov(Arrays.asList("Account", "Analytics", "Business_Unit", "Currency", "Customer", "Install_Country", "Product", "Project", "Region", "Scenario", "Version"))
                //.pov(Arrays.asList("AC_F990000000", "BU_7RENEW", "Products_Total", "Region", "Customers_Total", "No Function", "MTD", "Actual", "Projects_Total", "AY_Input", "USD", "IC_AMERICAS", "PC_Renewables","Final" ))
                .pov(Arrays.asList("BU_7RENEW", "AC_F990000000", "Products_Total", "No Region", "Customers_Total", "No Function", "MTD", "Actual", "Projects_Total", "AY_Input", "USD", "IC_AMERICAS", "PC_Renewables","Final" ))
                .left("Sep", "Oct")
                .top("FY19", "FY18")
                .build();
    }

    public DataSlice test(GridDefinition gridDefinition) {
        ExportDataSlice eds = new ExportDataSlice(gridDefinition);
        DataSlice slice = app.exportDataSlice("PlanPL", eds);
        slicePrinter.print(slice);
        return slice;
    }

}