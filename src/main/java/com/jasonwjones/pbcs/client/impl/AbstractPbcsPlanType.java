package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.api.v3.SubstitutionVariable;
import com.jasonwjones.pbcs.client.*;
import com.jasonwjones.pbcs.client.impl.grid.DataSliceGrid;

import java.util.List;
import java.util.Set;

/**
 * Provides a base implementation for implementors to build their own {@link PbcsPlanType} that implements a couple of
 * custom methods (such as to provide more aggressive caching on certain member operations).
 */
public class AbstractPbcsPlanType implements PbcsPlanType {

    protected final PbcsPlanType planType;

    public AbstractPbcsPlanType(PbcsPlanType planType) {
        this.planType = planType;
    }

    @Override
    public String getName() {
        return planType.getName();
    }

    @Override
    public List<PbcsDimension> getDimensions() {
        return planType.getDimensions();
    }

    @Override
    public List<PbcsJobDefinition> getJobs() {
        return planType.getJobs();
    }

    @Override
    public PbcsDimension getDimension(String dimensionName) {
        return planType.getDimension(dimensionName);
    }

    @Override
    public boolean isExplicitDimensions() {
        return planType.isExplicitDimensions();
    }

    @Override
    public PbcsApplication getApplication() {
        return planType.getApplication();
    }

    @Override
    public String getCell() {
        return planType.getCell();
    }

    @Override
    public ImportDataResult setCell(List<String> pov, String value) {
        return planType.setCell(pov, value);
    }

    @Override
    public ImportDataResult setCell(List<String> pov, String value, ImportDataOptions importDataOptions) {
        return planType.setCell(pov, value, importDataOptions);
    }

    @Override
    public ImportDataResult setCells(List<String> pov, Grid<String> values) {
        return planType.setCells(pov, values);
    }

    @Override
    public ImportDataResult setCells(List<String> pov, Grid<String> values, ImportDataOptions importDataOptions) {
        return planType.setCells(pov, values, importDataOptions);
    }

    @Override
    public String getCell(List<String> dataPoint) {
        return planType.getCell(dataPoint);
    }

    @Override
    public DataSliceGrid retrieve() {
        return planType.retrieve();
    }

    @Override
    public DataSliceGrid retrieve(List<String> dataPoint) {
        return planType.retrieve(dataPoint);
    }

    @Override
    public DataSliceGrid retrieve(List<String> pov, Grid<String> grid) {
        return planType.retrieve(pov, grid);
    }

    @Override
    public PbcsMemberProperties getMember(String dimensionName, String memberName) {
        return planType.getMember(dimensionName, memberName);
    }

    @Override
    public PbcsMemberProperties getMember(String memberName) {
        return planType.getMember(memberName);
    }

    @Override
    public List<PbcsMemberProperties> queryMembers(String memberName, PbcsMemberQueryType queryType) {
        return planType.queryMembers(memberName, queryType);
    }

    @Override
    public PbcsMemberProperties getMemberOrAlias(String memberOrAliasName) {
        return planType.getMemberOrAlias(memberOrAliasName);
    }

    @Override
    public Set<SubstitutionVariable> getSubstitutionVariables() {
        return planType.getSubstitutionVariables();
    }

}