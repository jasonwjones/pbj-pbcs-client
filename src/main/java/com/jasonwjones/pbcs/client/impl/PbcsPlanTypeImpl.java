package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.api.v3.dataslices.*;
import com.jasonwjones.pbcs.client.*;
import com.jasonwjones.pbcs.client.exceptions.PbcsClientException;
import com.jasonwjones.pbcs.client.impl.grid.DataSliceGrid;
import com.jasonwjones.pbcs.client.memberdimensioncache.InMemoryMemberDimensionCache;
import com.jasonwjones.pbcs.util.GridUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PbcsPlanTypeImpl implements PbcsPlanType {

	private static final Logger logger = LoggerFactory.getLogger(PbcsPlanTypeImpl.class);

	private final RestContext context;

	private final PbcsApplication application;

	private final String planType;

	private final List<PbcsDimension> explicitDimensions;

	private final MemberDimensionCache memberDimensionCache;

	PbcsPlanTypeImpl(RestContext context, PbcsApplication application, String planType) {
		this(context, application, planType, Collections.emptyList(), new InMemoryMemberDimensionCache());
	}

	PbcsPlanTypeImpl(RestContext context, PbcsApplication application, String planType, List<String> explicitDimensions, MemberDimensionCache memberDimensionCache) {
		if (explicitDimensions == null)
			throw new IllegalArgumentException("List of explicit dimensions cannot be null");
		this.context = context;
		this.application = application;
		this.planType = planType;
		this.explicitDimensions = new ArrayList<>();
		for (int index = 0; index < explicitDimensions.size(); index++) {
			this.explicitDimensions.add(new ExplicitDimension(explicitDimensions.get(index), index));
		}
		this.memberDimensionCache = memberDimensionCache;
	}

	@Override
	public String getName() {
		return this.planType;
	}

	@Override
	public List<PbcsDimension> getDimensions() {
		if (!explicitDimensions.isEmpty()) {
			return explicitDimensions;
		} else {
			return application.getDimensions(planType);
		}
	}

	@Override
	public PbcsDimension getDimension(String dimensionName) {
		for (PbcsDimension dimension : explicitDimensions) {
			if (dimension.getName().equals(dimensionName)) {
				return dimension;
			}
		}
		throw new IllegalArgumentException("No dimension " + dimensionName + " contained in dimension list");
	}

	@Override
	public PbcsApplication getApplication() {
		return this.application;
	}

	@Override
	public String getCell() {
		return getCell(dimensions());
	}

	@Override
	public String getCell(List<String> dataPoint) {
		String url = this.context.getBaseUrl() + "applications/{application}/plantypes/{planType}/exportdataslice";
		GridDefinition gridDefinition = new GridDefinition(dataPoint);
		ExportDataSlice exportDataSlice = new ExportDataSlice(gridDefinition);
		ResponseEntity<DataSlice> slice = this.context.getTemplate().postForEntity(url, exportDataSlice, DataSlice.class, application.getName(), planType);
		if (slice.getStatusCode().is2xxSuccessful()) {
			DataSlice dataSlice = slice.getBody();
			DataSlice.HeaderDataRow headerDataRow = dataSlice.getRows().get(0);
			return headerDataRow.getData().get(0);
		} else {
			throw new RuntimeException("Error retrieving data, received code: " + slice.getStatusCode());
		}
	}

	@Override
	public DataSliceGrid retrieve() {
		return retrieve(dimensions());
	}

	@Override
	public DataSliceGrid retrieve(List<String> dataPoint) {
		String url = this.context.getBaseUrl() + "applications/{application}/plantypes/{planType}/exportdataslice";
		GridDefinition gridDefinition = new GridDefinition(dataPoint);
		ExportDataSlice exportDataSlice = new ExportDataSlice(gridDefinition);
		ResponseEntity<DataSlice> slice = this.context.getTemplate().postForEntity(url, exportDataSlice, DataSlice.class, application.getName(), planType);
		if (slice.getStatusCode().is2xxSuccessful()) {
			DataSlice dataSlice = slice.getBody();
			return new DataSliceGrid(this, dataSlice);
		} else {
			throw new RuntimeException("Error retrieving data, received code: " + slice.getStatusCode());
		}
	}

	@Override
	public DataSliceGrid retrieve(List<String> pov, Grid<String> grid) {
		// get the 'fulcrum' point in the grid
		int firstRowWithCell = GridUtils.firstNonNullInColumn(grid, 0);
		int firstColWithCell = GridUtils.firstNonNullInRow(grid, 0);
		int lastNonNullCol = GridUtils.lastNonNullInRow(grid, 1);

		List<DimensionMembers> top = new ArrayList<>();
		for (int col = firstColWithCell; col <= lastNonNullCol; col++) {
			List<String> members = GridUtils.col(grid, col, 0, firstRowWithCell);
			DimensionMembers dimensionMembers = DimensionMembers.ofMemberNames(members);
			top.add(dimensionMembers);
		}

		List<DimensionMembers> left = new ArrayList<>();
		for (int row = firstRowWithCell; row < grid.getRows(); row++) {
			List<String> members = GridUtils.row(grid, row, 0, firstColWithCell);
			DimensionMembers dimensionMembers = DimensionMembers.ofMemberNames(members);
			left.add(dimensionMembers);
		}

		GridDefinition gridDefinition = new GridDefinition(pov, top, left);
		ExportDataSlice exportDataSlice = new ExportDataSlice(gridDefinition);

		try {
			ResponseEntity<DataSlice> slice = this.context.getTemplate().postForEntity(this.context.getBaseUrl() + "applications/{application}/plantypes/{planType}/exportdataslice", exportDataSlice, DataSlice.class, application.getName(), planType);
			if (slice.getStatusCode().is2xxSuccessful()) {
				DataSlice dataSlice = slice.getBody();
				return new DataSliceGrid(this, dataSlice);
			} else {
				throw new RuntimeException("Error retrieving data, received code: " + slice.getStatusCode());
			}
		} catch (Exception e) {
			logger.error("Exception: {}", e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}

	public void setCell(List<String> pov, String value) {
		ImportDataSlice importDataSlice = new ImportDataSlice(pov, value);
		logger.info("Updating {}.{} to set cell {} to {}", application.getName(), planType, pov, value);
		ResponseEntity<ImportDataSliceResponse> response = this.context.getTemplate().postForEntity(this.context.getBaseUrl() + "applications/{application}/plantypes/{planType}/importdataslice", importDataSlice, ImportDataSliceResponse.class, application.getName(), planType);

		if (response.getStatusCode().is2xxSuccessful()) {
			ImportDataSliceResponse importDataSliceResponse = response.getBody();
			logger.info("Update cell result: {} accepted cells, {} rejected cells", importDataSliceResponse.getNumAcceptedCells(), importDataSliceResponse.getNumRejectedCells());
		}
	}

	public void setCells(List<String> pov, Grid<String> values) {
		ImportDataSlice importDataSlice = new ImportDataSlice();
		importDataSlice.setDataGrid(new DataSlice(pov, values));
		logger.info("Updating {}.{} to with multiple data values", application.getName(), planType);
		ResponseEntity<ImportDataSliceResponse> response = this.context.getTemplate().postForEntity(this.context.getBaseUrl() + "applications/{application}/plantypes/{planType}/importdataslice", importDataSlice, ImportDataSliceResponse.class, application.getName(), planType);

		if (response.getStatusCode().is2xxSuccessful()) {
			ImportDataSliceResponse importDataSliceResponse = response.getBody();
			logger.info("Update cell result: {} accepted cells, {} rejected cells", importDataSliceResponse.getNumAcceptedCells(), importDataSliceResponse.getNumRejectedCells());
		}
	}

	@Override
	public PbcsMemberProperties getMember(String dimensionName, String memberName) {
		return application.getMember(dimensionName, memberName);
	}

	@Override
	public PbcsMemberProperties getMember(String memberName) {
		String dimensionName = findMemberDimension(memberName);
		if (dimensionName != null) {
			return getMember(dimensionName, memberName);
		} else {
			throw new PbcsClientException("Unable to determine dimension for member " + memberName);
		}
	}

	private String findMemberDimension(String memberName) {
		// possible TODO: if member name equals a dimension name, we could just shortcut
		String dimensionName = memberDimensionCache.getDimensionName(memberName);
		if (dimensionName == null) {
			logger.debug("Member dimension cache does not contain enty for {}, will search dimensions for it", memberName);
			for (PbcsDimension dimension : explicitDimensions) {
				try {
					PbcsMemberProperties memberProperties = getMember(dimension.getName(), memberName);
					if (memberProperties != null) {
						dimensionName = dimension.getName();
						memberDimensionCache.setDimension(memberName, dimensionName);
						break;
					}
				} catch (PbcsClientException e) {
					logger.debug("Did not find member {} in dimension {}", memberName, dimension);
				}
			}
		} else {
			logger.trace("Member {} has dimension {} from cache", memberName, dimensionName);
		}
		return dimensionName;
	}

	private List<String> dimensions() {
		return explicitDimensions.stream()
				.map(PbcsDimension::getName)
				.collect(Collectors.toList());
	}

	private class ExplicitDimension implements PbcsDimension {

		private final String name;

		private final int number;

		private ExplicitDimension(String name, int number) {
			this.name = name;
			this.number = number;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public int getNumber() {
			return number;
		}

		@Override
		public PbcsMemberProperties getMember(String memberName) {
			return PbcsPlanTypeImpl.this.getMember(name, memberName);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			ExplicitDimension that = (ExplicitDimension) o;
			return name.equals(that.name);
		}

		@Override
		public int hashCode() {
			return Objects.hash(name);
		}

	}

}