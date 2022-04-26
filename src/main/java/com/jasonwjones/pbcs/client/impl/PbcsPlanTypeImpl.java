package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.api.v3.dataslices.DataSlice;
import com.jasonwjones.pbcs.api.v3.dataslices.ExportDataSlice;
import com.jasonwjones.pbcs.api.v3.dataslices.GridDefinition;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsDimension;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.client.impl.grid.DataSliceGrid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.stream.Collectors;

public class PbcsPlanTypeImpl implements PbcsPlanType {

	private final RestContext context;

	private final PbcsApplication application;

	private final String planType;

	private final List<String> explicitDimensions;

	PbcsPlanTypeImpl(RestContext context, PbcsApplication application, String planType) {
		this(context, application, planType, Collections.emptyList());
	}

	PbcsPlanTypeImpl(RestContext context, PbcsApplication application, String planType, List<String> explicitDimensions) {
		if (explicitDimensions == null) throw new IllegalArgumentException("List of explicit dimensions cannot be null");
		this.context = context;
		this.application = application;
		this.planType = planType;
		this.explicitDimensions = new ArrayList<>(explicitDimensions);
	}

	@Override
	public String getName() {
		return this.planType;
	}

	@Override
	public List<PbcsDimension> getDimensions() {
		if (!explicitDimensions.isEmpty()) {
			return explicitDimensions.stream()
					.map(ExplicitDimension::new)
					.collect(Collectors.toList());
		} else {
			return application.getDimensions(planType);
		}
	}

	@Override
	public PbcsApplication getApplication() {
		return this.application;
	}

	@Override
	public String getCell() {
		return getCell(explicitDimensions);
	}

	@Override
	public String getCell(List<String> dataPoint) {
		String url = this.context.getBaseUrl() + "applications/{application}/plantypes/{planType}/exportdataslice";
		GridDefinition gridDefinition = new GridDefinition(dataPoint);
		ExportDataSlice exportDataSlice = new ExportDataSlice(gridDefinition);
		ResponseEntity<DataSlice> slice = this.context.getTemplate().postForEntity(url, exportDataSlice, DataSlice.class, application.getName(), planType);
		if (slice.getStatusCode() == HttpStatus.OK) {
			DataSlice dataSlice = slice.getBody();
			DataSlice.HeaderDataRow headerDataRow = dataSlice.getRows().get(0);
			return headerDataRow.getData().get(0);
		} else {
			throw new RuntimeException("Error retrieving data, received code: " + slice.getStatusCode());
		}
	}

	public DataSliceGrid retrieve(List<String> dataPoint) {
		String url = this.context.getBaseUrl() + "applications/{application}/plantypes/{planType}/exportdataslice";
		GridDefinition gridDefinition = new GridDefinition(dataPoint);
		ExportDataSlice exportDataSlice = new ExportDataSlice(gridDefinition);
		ResponseEntity<DataSlice> slice = this.context.getTemplate().postForEntity(url, exportDataSlice, DataSlice.class, application.getName(), planType);
		if (slice.getStatusCode() == HttpStatus.OK) {
			DataSlice dataSlice = slice.getBody();
			return new DataSliceGrid(dataSlice);
		} else {
			throw new RuntimeException("Error retrieving data, received code: " + slice.getStatusCode());
		}
	}

	private static class ExplicitDimension implements PbcsDimension {

		private final String name;

		private ExplicitDimension(String name) {
			this.name = name;
		}

		@Override
		public String getBalanceColumnName() {
			return null;
		}

		@Override
		public String getDimensionClass() {
			return null;
		}

		@Override
		public String getDimensionClassOrg() {
			return null;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public String getNameOrg() {
			return null;
		}

		@Override
		public Set<String> getValidPlans() {
			return null;
		}

		@Override
		public boolean isValidForPlan(String plan) {
			return false;
		}

	}

}