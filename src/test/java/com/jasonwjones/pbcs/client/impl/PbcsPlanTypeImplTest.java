package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.api.v3.SubstitutionVariable;
import com.jasonwjones.pbcs.api.v3.dataslices.DimensionMembers;
import com.jasonwjones.pbcs.client.PbcsAppDimension;
import com.jasonwjones.pbcs.client.PbcsJobDefinition;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.client.PbcsPov;
import com.jasonwjones.pbcs.client.exceptions.PbcsDataImportException;
import com.jasonwjones.pbcs.client.exceptions.PbcsGeneralException;
import com.jasonwjones.pbcs.client.impl.export.MarkdownExportCallback;
import com.jasonwjones.pbcs.client.impl.grid.DataSliceGrid;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThrows;

public class PbcsPlanTypeImplTest extends AbstractVisionCubeIT {

    private static final Logger logger = LoggerFactory.getLogger(PbcsPlanTypeImplTest.class);

    @Test
    public void whenRetrieveNormal() {
        DataSliceGrid grid = cube.retrieve(Arrays.asList("Actual", "Final", "P_000", "FY21", "4110", "USD", "000", "IDescendants(Period)"));
        grid.print();
        assertThat(grid.getPov(), hasSize(6)); // not a great test but good enough for now
    }

    @Test
    public void whenRetrieveWithInvalidIntersection() {
        List<String> badPov = Arrays.asList("XActual", "Final", "P_000", "FY21", "4110", "USD", "000", "IDescendants(Period)");
        assertThrows(PbcsGeneralException.class, () -> cube.retrieve(badPov));
    }

    @Test
    public void getCell() {
        assertThat(cube.getCell(), is(cube.getCell(DIMENSIONS)));
    }

    @Test
    public void setCell() {
        PbcsPlanType.ImportDataResult result = cube.setCell(LEVEL0_TEST_CELL, CELL_TEST_VALUE);
        assertThat(CELL_TEST_VALUE, is(cube.getCell(LEVEL0_TEST_CELL)));
        assertThat(1, is(result.getAcceptedCells()));
    }

    @Test
    public void whenSetCellMissing() {
        cube.setCell(LEVEL0_TEST_CELL, CELL_TEST_VALUE);
        assertThat(CELL_TEST_VALUE, is(cube.getCell(LEVEL0_TEST_CELL)));

        PbcsPlanType.ImportDataResult result = cube.setCell(LEVEL0_TEST_CELL, PbcsPlanType.IMPORT_MISSING);
        assertThat(cube.getCell(LEVEL0_TEST_CELL), is(PbcsPlanType.EXPORT_MISSING));
        assertThat(1, is(result.getAcceptedCells()));
    }

    @Test
    public void whenSetCellBlank() {
        cube.setCell(LEVEL0_TEST_CELL, CELL_TEST_VALUE);
        assertThat(CELL_TEST_VALUE, is(cube.getCell(LEVEL0_TEST_CELL)));

        PbcsPlanType.ImportDataResult result = cube.setCell(LEVEL0_TEST_CELL, PbcsPlanType.EXPORT_MISSING);
        assertThat(cube.getCell(LEVEL0_TEST_CELL), is(PbcsPlanType.EXPORT_MISSING));
        assertThat(1, is(result.getAcceptedCells()));
    }

    @Test
    public void setCellUpperLevel() {
        PbcsPlanType.ImportDataResult result = cube.setCell(UPPER_LEVEL_TEST_CELL, CELL_TEST_VALUE);
        assertThat(1, is(result.getRejectedCells()));
        assertThat(0, is(result.getAcceptedCells()));
    }

    @Test(expected = PbcsDataImportException.class)
    public void rejectedCellsThrowsException() {
        PbcsPlanTypeImpl.ImportDataOptionsImpl options = new PbcsPlanTypeImpl.ImportDataOptionsImpl();
        options.setThrowExceptionIfAnyRejectedCells(true);
        cube.setCell(UPPER_LEVEL_TEST_CELL, CELL_TEST_VALUE, options);
    }

    @Test
    public void setCellDryRun() {
        cube.setCell(LEVEL0_TEST_CELL, CELL_TEST_VALUE);

        PbcsPlanTypeImpl.ImportDataOptionsImpl options = new PbcsPlanTypeImpl.ImportDataOptionsImpl();
        options.setDryRun(true);
        cube.setCell(LEVEL0_TEST_CELL, "#Missing", options);

        // expecting that setting missing did not blank the cell since dry run is on
        assertThat(cube.getCell(LEVEL0_TEST_CELL), is(CELL_TEST_VALUE));
    }

    @Test
    public void whenExport() {
        PbcsPov pov = cube.createPov()
                .without("Period")
                .without("Account");

        DimensionMembers dm = DimensionMembers.of("4110");
        dm.setDimensions(Collections.singletonList("Account"));
        show(pov, "Lvl0Descendants(YearTotal)", dm);
    }

    @Test
    public void whenExportMultipleMembers() {
        PbcsPov pov = cube.createPov("Actual", "FY22", "Working", "USD", "TD", "P_TP")
                .without("Period")
                .without("Account");

        //DimensionMembers dm = DimensionMembers.ofSingleDimension("NI", "GP", "4001", "OpEx");
        DimensionMembers dm = DimensionMembers.ofSingleDimension("Children(4001)");
        dm.setDimensions(Collections.singletonList("Account"));
        show(pov, "Lvl0Descendants(YearTotal)", dm);
    }

    @Test
    public void whenExportMultipleEntities() {
        PbcsPov pov = cube.createPov("Actual", "FY22", "Working", "USD", "P_TP", "NI")
                .without("Period")
                .without("Entity");

        DimensionMembers dm = DimensionMembers.ofSingleDimension("Children(TD)");
        dm.setDimensions(Collections.singletonList("Account"));
        show(pov, "Lvl0Descendants(YearTotal)", dm);
    }

    @Test
    public void whenExportMultipleProducts() {
        PbcsPov pov = cube.createPov("Actual", "FY22", "Working", "USD", "Total Entity", "4001")
                .without("Period")
                .without("Product");

        DimensionMembers dm = DimensionMembers.ofSingleDimension("Descendants(Product)");
        dm.setDimensions(Collections.singletonList("Product"));
        show(pov, "Lvl0Descendants(YearTotal)", dm);
    }

    @Test
    public void whenGetSubstitutionVariables() {
        Set<SubstitutionVariable> vars = app.getSubstitutionVariables();
        System.out.println("Count of variables: " + vars.size());
        for (SubstitutionVariable var : vars) {
            logger.info("Variable: {}", var);
        }
    }

    @Test
    public void whenGetPlanVariables() {
        for (PbcsPlanType planType : app.getPlanTypes()) {
            logger.info("Sub vars in {}", planType);
            for (SubstitutionVariable variable : planType.getSubstitutionVariables()) {
                logger.info(" Var: {}", variable);
            }
        }
    }

    @Ignore // re-add when real testing happens
    @Test
    public void whenExportMetadata() {
        app.exportMetadata("ExportProduct", "test.zip");
    }

    @Test
    public void whenGetDimensions() {
        logger.info("Dims in application: {}", app.getName());
        for (PbcsAppDimension dimension : app.getDimensions()) {
            logger.info("Dimension: {}, valid in: {}", dimension.getName(), dimension.getValidPlans());
        }
        assertThat(app.getDimensions(), hasSize(8));
    }

    @Test
    public void whenGetJobs() {
        List<PbcsJobDefinition> jobDefinitions = app.getJobDefinitions();
        jobDefinitions.forEach(jd -> logger.info("Job: {}", jd));
    }

    private void show(PbcsPov pov, String header, DimensionMembers dm) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            //PbcsPlanType.ExportCallback exportCallback = new PrintStreamExportCallback(baos);
            PbcsPlanType.ExportCallback exportCallback = new MarkdownExportCallback(baos);
            cube.export(pov, header, dm, exportCallback);
            System.out.println("====");
            baos.writeTo(System.out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}