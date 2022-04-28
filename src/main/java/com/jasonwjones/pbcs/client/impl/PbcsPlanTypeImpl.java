package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.api.v3.dataslices.DataSlice;
import com.jasonwjones.pbcs.api.v3.dataslices.ExportDataSlice;
import com.jasonwjones.pbcs.api.v3.dataslices.GridDefinition;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsDimension;
import com.jasonwjones.pbcs.client.PbcsMemberProperties;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.client.impl.grid.DataSliceGrid;
import com.jasonwjones.pbcs.client.impl.models.PbcsMemberPropertiesImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

public class PbcsPlanTypeImpl implements PbcsPlanType {

	private static final Logger logger = LoggerFactory.getLogger(PbcsPlanTypeImpl.class);

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

	public PbcsMemberProperties getMember(String dimensionName, String memberName) {
		Assert.hasText(dimensionName, "Must specify a dimension name");
		Assert.hasText(memberName, "Must specify a member name");

		logger.debug("Fetching member properties for {} from dimension {}", memberName, dimensionName);
		String url = this.context.getBaseUrl() + "applications/{application}/dimensions/{dimName}/members/{member}";
		ResponseEntity<PbcsMemberPropertiesImpl> memberResponse = this.context.getTemplate().getForEntity(url, PbcsMemberPropertiesImpl.class, application.getName(), dimensionName, memberName);
		if (memberResponse.getStatusCode().is2xxSuccessful()) {
			return memberResponse.getBody();
		} else {
			logger.warn("Response from member fetch operation for {} in dimension {} was {}", memberName, dimensionName, memberResponse.getStatusCodeValue());
			return null;
		}
	}

	public PbcsMemberProperties getMember(String memberName) {
		for (String dimension : explicitDimensions) {
			PbcsMemberProperties memberProperties = getMember(dimension, memberName);
			if (memberProperties != null) {
				return memberProperties;
			}
		}
		return null;
	}

	private class ExplicitDimension implements PbcsDimension {

		private final String name;

		private ExplicitDimension(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public PbcsMemberProperties getMember(String memberName) {
			return PbcsPlanTypeImpl.this.getMember(name, memberName);
		}

	}

}