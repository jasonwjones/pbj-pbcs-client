package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.client.Grid;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.client.PovGrid;
import com.jasonwjones.pbcs.client.impl.grid.DataSliceGrid;
import com.jasonwjones.pbcs.client.impl.grid.DataSliceGridPrinter;
import com.jasonwjones.pbcs.util.GridPrinter;
import com.jasonwjones.pbcs.util.GridUtils;
import com.jasonwjones.pbcs.util.TextGridReader;
import com.jasonwjones.pbcs.testing.ReadOnlyIntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.jasonwjones.pbcs.matchers.DataSliceGridMatcher.hasDimensions;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Notes on ad hoc options:
 *
 * <pre>
*  <EssProperties>
 *       <IndentStyle>3</IndentStyle>
 *       <MissingTextString>0</MissingTextString>
 *       <NoAccessString>#NoAccess</NoAccessString>
 *       <AliasTable>Default</AliasTable>
 *       <LatestMemberName />
 *       <DrillLevel>1</DrillLevel>
 *       <SuppressMissing>false</SuppressMissing>
 *       <SuppressZeros>false</SuppressZeros>
 *       <SuppressUnderscores>false</SuppressUnderscores>
 *       <RepeatMemberNames>false</RepeatMemberNames>
 *       <UseAliases>true</UseAliases>
 *       <UseBothForRowDimensions>false</UseBothForRowDimensions>
 *       <SpecifyLatestMember>false</SpecifyLatestMember>
 *       <IncludeSelection>true</IncludeSelection>
 *       <SelectedGroup>false</SelectedGroup>
 *       <SelectionOnly>false</SelectionOnly>
 *       <SendLevelZeroOnly>false</SendLevelZeroOnly>
 *       <SendZerosAsMissing>true</SendZerosAsMissing>
 *       <SendBlanksAsMissing>true</SendBlanksAsMissing>
 *       <StrictMode>false</StrictMode>
 *       <UpdateMode>false</UpdateMode>
 *       <FreeFormMode>false</FreeFormMode>
 *       <TemplateRetrieveMode>false</TemplateRetrieveMode>
 *       <AutoSortRows>false</AutoSortRows>
 *       <DatalessNavigation>false</DatalessNavigation>
 *       <HybridAnalysisEnabled>false</HybridAnalysisEnabled>
 *       <UseSmartLists>false</UseSmartLists>
 *       <UseSmartLists_ShowDropDownList>false</UseSmartLists_ShowDropDownList>
 *       <EmptyGridError>false</EmptyGridError>
 *       <CultureCountryRegion>US</CultureCountryRegion>
 *       <CultureLanguage>en</CultureLanguage>
 *       <AncestorOnTop>false</AncestorOnTop>
 *       <DrillthroughEnabled>false</DrillthroughEnabled>
 * </EssProperties>
 * </pre>
 */
@Category(ReadOnlyIntegrationTest.class)
public class PbcsPlanTypeAdhocIT extends AbstractVisionCubeIT {

    protected PbcsPlanType cube;

    public static final List<String> BASE_DIMENSIONS = Arrays.asList("Account", "Currency", "Entity", "Period", "Product", "Scenario", "Version", "Year");

    /**
     * Nearly identical to the LEVEL0 test cell, except we have an upper-level account (7001)
     */
    public static final List<String> UPPER_LEVEL_TEST_CELL = Arrays.asList("Actual", "FY21", "Final", "USD", "000", "P_000", "Jan", "7001");

    public static final String CELL_TEST_VALUE = "2";

    @Test
    public void getCell() throws IOException {
        Grid<String> grid = new TextGridReader().read("grids/simple4.txt");
        List<String> pov = new ArrayList<>(GridUtils.nonNullRowItems(grid, 0));
        Grid<String> actualGrid = GridUtils.subgrid(grid, 1);

        GridPrinter.print(grid);

        DataSliceGrid dataSliceGrid = cube.retrieve(pov, actualGrid);
        DataSliceGridPrinter.print(dataSliceGrid);
    }

    // this is a quick test to ensure that PBCS supports retrieving without a POV (and therefore, a fully-qualified
    // or fully-stacked grid
    @Test
    public void getFullyStacked() throws IOException {
        Grid<String> grid = new TextGridReader().read("grids/fully-stacked.txt");
        GridPrinter.print(grid);

        DataSliceGrid dataSliceGrid = cube.retrieve(Collections.emptyList(), grid);
        DataSliceGridPrinter.print(dataSliceGrid);
    }

    @Test
    public void whenGetCell() {
        assertThat(cube.getCell(BASE_DIMENSIONS), is("")); // ?
        assertThat(cube.getCell(LEVEL0_TEST_CELL), is(CELL_TEST_VALUE));
    }

    @Test
    public void retrieveWithAttributes() throws IOException {
        Grid<String> grid = new TextGridReader().read("grids/simple-attribute2.txt");
        List<String> pov = new ArrayList<>(GridUtils.nonNullRowItems(grid, 0));
        Grid<String> actualGrid = GridUtils.subgrid(grid, 1);

        GridPrinter.print(grid);

        DataSliceGrid dataSliceGrid = cube.retrieve(pov, actualGrid);
        DataSliceGridPrinter.print(dataSliceGrid);
    }

    @Test
    public void retrieveWithSupportingDetail() throws IOException {
        PovGrid<String> grid = new TextGridReader().readPovGridFromFile("grids/supporting-detail.txt");
        PbcsRetrieveOptionsImpl retrieveOptions = new PbcsRetrieveOptionsImpl();
        retrieveOptions.setExportPlanningData(true);
        GridPrinter.print(grid);

        DataSliceGrid dataSliceGrid = cube.retrieve(grid, retrieveOptions);
        DataSliceGridPrinter.print(dataSliceGrid);
    }

    @Test
    public void retrieveWithSmartList() throws IOException {
        PovGrid<String> grid = new TextGridReader().readPovGridFromFile("grids/retrieve-smartlist.txt");
        PbcsRetrieveOptionsImpl retrieveOptions = new PbcsRetrieveOptionsImpl();
        retrieveOptions.setExportPlanningData(true);
        GridPrinter.print(grid);

        DataSliceGrid dataSliceGrid = cube.retrieve(grid, retrieveOptions);
        DataSliceGridPrinter.print(dataSliceGrid);
    }

    @Test
    // -Dhttps.proxyHost=localhost -Dhttps.proxyPort=8080
    public void retrieveWithAttributesUseHinting() {
        PovGrid<String> grid = grid("grids/simple-attribute2.txt");
        DataSliceGrid dataSliceGrid = retrieve(grid);
        // can't use until we read a PovGrid directly and chop the POV
        //assertThat(dataSliceGrid, hasDimensions(grid));
        assertThat(dataSliceGrid, hasDimensions(3, 3));
    }

    public PovGrid<String> grid(String resourceName) {
        try {
            Grid<String> grid = new TextGridReader().read(resourceName);
            List<String> pov = new ArrayList<>(GridUtils.nonNullRowItems(grid, 0));
            Grid<String> actualGrid = GridUtils.subgrid(grid, 1);
            return new PovGridImpl<>(pov, actualGrid);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read grid from " + resourceName + ": " + e.getMessage());
        }
    }

    private DataSliceGrid retrieve(PovGrid<String> grid) {
        GridPrinter.print(grid);

        DataSliceGrid dataSliceGrid = cube.retrieve(grid, null);
        DataSliceGridPrinter.print(dataSliceGrid);
        return dataSliceGrid;
    }

}
