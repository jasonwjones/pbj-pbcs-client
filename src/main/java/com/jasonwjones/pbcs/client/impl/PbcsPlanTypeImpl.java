package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.api.v3.SubstitutionVariable;
import com.jasonwjones.pbcs.api.v3.SubstitutionVariablesWrapper;
import com.jasonwjones.pbcs.api.v3.dataslices.*;
import com.jasonwjones.pbcs.client.*;
import com.jasonwjones.pbcs.client.exceptions.PbcsClientException;
import com.jasonwjones.pbcs.client.exceptions.PbcsDataImportException;
import com.jasonwjones.pbcs.client.exceptions.PbcsNoSuchObjectException;
import com.jasonwjones.pbcs.client.impl.grid.DataSliceGrid;
import com.jasonwjones.pbcs.client.memberdimensioncache.InMemoryMemberDimensionCache;
import com.jasonwjones.pbcs.util.GridUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.stream.Collectors;

public class PbcsPlanTypeImpl extends AbstractPbcsObject implements PbcsPlanType {

	private static final Logger logger = LoggerFactory.getLogger(PbcsPlanTypeImpl.class);

	public static final ImportDataOptions DEFAULT_IMPORT_OPTIONS = new ImportDataOptionsImpl();

	private final PbcsApplication application;

	private final String planType;

	protected final MemberDimensionCache memberDimensionCache;

	PbcsPlanTypeImpl(RestContext context, PbcsApplication application, String planType) {
		this(context, application, planType, new InMemoryMemberDimensionCache());
	}

	PbcsPlanTypeImpl(RestContext context, PbcsApplication application, String planType, MemberDimensionCache memberDimensionCache) {
		super(context);
		this.application = application;
		this.planType = planType;
		this.memberDimensionCache = memberDimensionCache;
	}

	@Override
	public String getName() {
		return this.planType;
	}

	@Override
	public PbcsObjectType getObjectType() {
		return PbcsObjectType.PLAN;
	}

	@Override
	public List<PbcsDimension> getDimensions() {
		return application.getDimensions(planType);
	}

	@Override
	public List<PbcsJobDefinition> getJobs() {
		return application.getJobDefinitions().stream()
				.filter(job -> job.getPlanTypeName().equals(planType))
				.collect(Collectors.toList());
	}

	@Override
	public PbcsDimension getDimension(String dimensionName) {
		throw new IllegalArgumentException("Cannot get dimension in non-explicit dimension plan type");
	}

	@Override
	public boolean isExplicitDimensions() {
		return false;
	}

	@Override
	public PbcsApplication getApplication() {
		return this.application;
	}

	@Override
	public String getCell() {
		throw new UnsupportedOperationException("Cannot get default cell when using plan without explicit dimensions");
	}

	@Override
	public String getCell(List<String> dataPoint) {
		// just lean on the implementation available in the application to avoid duplication
		DataSlice dataSlice = this.application.exportDataSlice(getName(), new ExportDataSlice(new GridDefinition(dataPoint)));
		DataSlice.HeaderDataRow headerDataRow = dataSlice.getRows().get(0);
		return headerDataRow.getData().get(0);
	}

	@Override
	public DataSliceGrid retrieve() {
		throw new UnsupportedOperationException("Cannot retrieve default cell when using plan without explicit dimensions");
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
		int lastNonNullCol = GridUtils.lastNonNullInRow(grid, 0);

		List<DimensionMembers> top = new ArrayList<>();
		for (int col = firstColWithCell; col <= lastNonNullCol; col++) {
			List<String> members = GridUtils.col(grid, col, 0, firstRowWithCell);
			DimensionMembers dimensionMembers = DimensionMembers.ofMemberNames(members);
			top.add(dimensionMembers);
		}

		List<DimensionMembers> left = new ArrayList<>();
		List<List<String>> columns = new ArrayList<>();
		for (int col = 0; col < firstColWithCell; col++) {
			List<String> colMembers = GridUtils.col(grid, col, firstRowWithCell, grid.getRows());
			columns.add(colMembers);
		}
		DimensionMembers leftDimMembers = DimensionMembers.of(columns);
		left.add(leftDimMembers);

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

	@Override
	public DataSliceGrid retrieve(PovGrid<String> grid, RetrieveOptions options) {
		throw new UnsupportedOperationException("Can only retrieve with options on explicit dimension plan");
	}

	@Override
	public ImportDataResult setCell(List<String> pov, String value) {
		return setCell(pov, value, DEFAULT_IMPORT_OPTIONS);
	}

	@Override
	public ImportDataResult setCell(List<String> pov, String value, ImportDataOptions importDataOptions) {
		ImportDataSlice importDataSlice = new ImportDataSlice(pov, value);
		logger.info("Updating {}.{} to set cell {} to {}", application.getName(), planType, pov, value);
		return importDataSlice(importDataSlice, importDataOptions);
	}

	@Override
	public ImportDataResult setCells(List<String> pov, Grid<String> values) {
		return setCells(pov, values, DEFAULT_IMPORT_OPTIONS);
	}

	@Override
	public ImportDataResult setCells(List<String> pov, Grid<String> values, ImportDataOptions importDataOptions) {
		ImportDataSlice importDataSlice = new ImportDataSlice();
		importDataSlice.setDataGrid(new DataSlice(pov, values));
		logger.info("Updating {}.{} at POV {} using a {}x{} source grid", application.getName(), planType, pov, values.getRows(), values.getColumns());
		return importDataSlice(importDataSlice, importDataOptions);
	}

	private ImportDataResult importDataSlice(ImportDataSlice importDataSlice, ImportDataOptions importDataOptions) {

		importDataSlice.setAggregateEssbaseData(importDataOptions.isAggregateData());
		importDataSlice.setCellNotesOption(importDataOptions.getCellNotesOption().getApiCode());
		importDataSlice.setDateFormat(importDataOptions.getDateFormat());
		importDataSlice.setDryRun(importDataOptions.isDryRun());
		importDataSlice.setStrictDateValidation(importDataOptions.isStrictDateValidation());
		importDataSlice.getCustomParams().setPostDataImportRuleNames(importDataOptions.getPostDataImportRuleNames());
		importDataSlice.getCustomParams().setIncludeRejectedCells(importDataOptions.isIncludeRejectedCells());
		importDataSlice.getCustomParams().setIncludeRejectedCellsWithDetails(importDataOptions.isIncludeRejectedCellsWithDetails());

		ResponseEntity<ImportDataSliceResponse> response = this.context.getTemplate().postForEntity(this.context.getBaseUrl() + "applications/{application}/plantypes/{planType}/importdataslice", importDataSlice, ImportDataSliceResponse.class, application.getName(), planType);
		if (response.getStatusCode().is2xxSuccessful()) {
			ImportDataSliceResponse importDataSliceResponse = response.getBody();
			logger.info("Update cell result: {} accepted cells, {} rejected cells", importDataSliceResponse.getNumAcceptedCells(), importDataSliceResponse.getNumRejectedCells());
			if (importDataOptions.isThrowExceptionIfAnyRejectedCells() && importDataSliceResponse.getNumRejectedCells() > 0) {
				throw new PbcsDataImportException(importDataSliceResponse);
			}
			if (importDataSliceResponse.getNumRejectedCells() > 0 && importDataSliceResponse.getRejectedCellsWithDetails() != null) {
				for (ImportDataSliceResponse.RejectedCellDetails rejectedCellDetails : importDataSliceResponse.getRejectedCellsWithDetails()) {
					logger.warn("Unable to update cell at {}; read only reason: {}, other reasons: {}", rejectedCellDetails.getMemberNames(), rejectedCellDetails.getReadOnlyReasons(), rejectedCellDetails.getOtherReasons());
				}
			}
			return new ImportDataResultImpl(importDataSliceResponse);
		} else {
			throw new PbcsClientException("Data slice import was unsuccessful: " + response.getStatusCode());
		}
	}

	@Override
	public PbcsMemberProperties getMember(String dimensionName, String memberName) {
		return application.getMember(dimensionName, memberName);
	}

	@Override
	public PbcsMemberProperties getMember(String memberName) {
		String dimensionName = findMemberDimensionFromCache(memberName);
		if (dimensionName != null) {
			return getMember(dimensionName, memberName);
		} else {
			throw new PbcsClientException("Unable to determine dimension for member (try using explicit dimensions plan type)" + memberName);
		}
	}

	@Override
	public List<PbcsMemberProperties> queryMembers(String memberName, PbcsMemberQueryType queryType) {
		PbcsMemberProperties member = getMemberOrAlias(memberName);
		if (member == null) throw new PbcsNoSuchObjectException(memberName, PbcsObjectType.MEMBER);

		List<PbcsMemberProperties> results = new ArrayList<>();

		switch (queryType) {
			case ICHILDREN:
				results.add(member);
			case CHILDREN:
				for (PbcsMemberProperties child : member.getChildren()) {
					results.add(child);
				}
				break;
			case IDESCENDANTS:
				results.add(member);
			case DESCENDANTS:
				// do first iteration ourselves here so that resulting list doesn't include root member
				for (PbcsMemberProperties child : member.getChildren()) {
					processChildren(results, child);
				}
				break;
			case IANCESTORS:
				results.add(member);
			case ANCESTORS:
				while (member.getParentName() != null) {
					PbcsMemberProperties parent = getMember(member.getDimensionName(), member.getParentName());
					results.add(parent);
					member = parent;
				}
				break;
			case ISIBLINGS:
			case SIBLINGS:
				PbcsMemberProperties parent = getMember(member.getDimensionName(), member.getParentName());
				if (queryType.isIncludeOriginalMember()) {
					results.addAll(parent.getChildren());
				} else {
					for (PbcsMemberProperties sibling : parent.getChildren()) {
						if (!sibling.getName().equals(memberName)) {
							results.add(sibling);
						}
					}
				}
				break;
		}

		return Collections.unmodifiableList(results);
	}

	@Override
	public List<PbcsMemberProperties> searchMembers(MemberSearchQuery query) {
		throw new UnsupportedOperationException();
	}

	private static void processChildren(List<PbcsMemberProperties> members, PbcsMemberProperties currentMember) {
		members.add(currentMember);
		for (PbcsMemberProperties child : currentMember.getChildren()) {
			processChildren(members, child);
		}
	}

	@Override
	public Set<SubstitutionVariable> getSubstitutionVariables() {
		String url = this.context.getBaseUrl() + "applications/{application}/plantypes/{planType}/substitutionvariables";
		ResponseEntity<SubstitutionVariablesWrapper> response = this.context.getTemplate().getForEntity(url, SubstitutionVariablesWrapper.class, application.getName(), getName());
		return new HashSet<>(response.getBody().getItems());
	}

	@Override
	public PbcsMemberProperties getMemberOrAlias(String memberOrAliasName) {
		throw new IllegalStateException("Must configure explicit dimensions to search for alias");
	}

	public String findMemberDimensionFromCache(String memberName) {
		String dimensionName = memberDimensionCache.getDimensionName(this, memberName);
		if (dimensionName == null) {
			logger.warn("Tried to find dimension for member {} but this is not an explicit dimensions plan type and the member-dimension cache did not resolve the dimension", memberName);
		} else {
			logger.trace("Member {} has dimension {} from cache", memberName, dimensionName);
		}
		return dimensionName;
	}

	private static class ImportDataResultImpl implements ImportDataResult {

		private final ImportDataSliceResponse response;

		public ImportDataResultImpl(ImportDataSliceResponse response) {
			this.response = response;
		}

		public int getAcceptedCells() {
			return response.getNumAcceptedCells();
		}

		public int getRejectedCells() {
			return response.getNumRejectedCells();
		}

	}

	public static class ImportDataOptionsImpl implements ImportDataOptions {

		private boolean aggregateData;

		private CellNotesOption cellNotesOption = CellNotesOption.SKIP;

		private String dateFormat = "DD/MM/YYYY";

		private boolean strictDateValidation = true;

		private boolean dryRun;

		private boolean includeRejectedCells = true;

		private boolean includeRejectedCellsWithDetails = false;

		private String postDataImportRuleNames;

		private boolean throwExceptionIfAnyRejectedCells;

		@Override
		public boolean isAggregateData() {
			return aggregateData;
		}

		public void setAggregateData(boolean aggregateData) {
			this.aggregateData = aggregateData;
		}

		@Override
		public CellNotesOption getCellNotesOption() {
			return cellNotesOption;
		}

		public void setCellNotesOption(CellNotesOption cellNotesOption) {
			this.cellNotesOption = cellNotesOption;
		}

		@Override
		public String getDateFormat() {
			return dateFormat;
		}

		public void setDateFormat(String dateFormat) {
			this.dateFormat = dateFormat;
		}

		@Override
		public boolean isStrictDateValidation() {
			return strictDateValidation;
		}

		public void setStrictDateValidation(boolean strictDateValidation) {
			this.strictDateValidation = strictDateValidation;
		}

		@Override
		public boolean isDryRun() {
			return dryRun;
		}

		public void setDryRun(boolean dryRun) {
			this.dryRun = dryRun;
		}

		@Override
		public boolean isIncludeRejectedCells() {
			return includeRejectedCells;
		}

		public void setIncludeRejectedCells(boolean includeRejectedCells) {
			this.includeRejectedCells = includeRejectedCells;
		}

		@Override
		public boolean isIncludeRejectedCellsWithDetails() {
			return includeRejectedCellsWithDetails;
		}

		public void setIncludeRejectedCellsWithDetails(boolean includeRejectedCellsWithDetails) {
			this.includeRejectedCellsWithDetails = includeRejectedCellsWithDetails;
		}

		@Override
		public String getPostDataImportRuleNames() {
			return postDataImportRuleNames;
		}

		public void setPostDataImportRuleNames(String postDataImportRuleNames) {
			this.postDataImportRuleNames = postDataImportRuleNames;
		}

		@Override
		public boolean isThrowExceptionIfAnyRejectedCells() {
			return throwExceptionIfAnyRejectedCells;
		}

		public void setThrowExceptionIfAnyRejectedCells(boolean throwExceptionIfAnyRejectedCells) {
			this.throwExceptionIfAnyRejectedCells = throwExceptionIfAnyRejectedCells;
		}

	}

}