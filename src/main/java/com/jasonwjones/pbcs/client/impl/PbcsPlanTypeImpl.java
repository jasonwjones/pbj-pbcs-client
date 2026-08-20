package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.api.v3.PlanTypeDimension;
import com.jasonwjones.pbcs.api.v3.PlanTypeDimensionsWrapper;
import com.jasonwjones.pbcs.api.v3.SubstitutionVariable;
import com.jasonwjones.pbcs.api.v3.SubstitutionVariablesWrapper;
import com.jasonwjones.pbcs.api.v3.dataslices.*;
import com.jasonwjones.pbcs.client.*;
import com.jasonwjones.pbcs.client.exceptions.PbcsClientException;
import com.jasonwjones.pbcs.client.exceptions.PbcsDataExportException;
import com.jasonwjones.pbcs.client.exceptions.PbcsDataImportException;
import com.jasonwjones.pbcs.client.exceptions.PbcsInvalidDimensionException;
import com.jasonwjones.pbcs.client.exceptions.PbcsNoSuchObjectException;
import com.jasonwjones.pbcs.client.impl.grid.DataSliceGrid;
import com.jasonwjones.pbcs.util.DataSliceDiff;
import com.jasonwjones.pbcs.util.GridDrawing;
import com.jasonwjones.pbcs.util.GridUtils;
import com.jasonwjones.pbcs.util.NumberUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Default {@link PbcsPlanType} implementation. {@link #getDimensions()} is backed by the official,
 * plan-type-scoped dimension list endpoint; {@link PbcsApplication.PlanTypeConfiguration#isQueryDimensions()}
 * remains backed by the older, unofficial data management (DM/AIF) endpoint for compatibility.
 */
public class PbcsPlanTypeImpl extends AbstractPbcsObject implements PbcsPlanType {

	private static final Logger logger = LoggerFactory.getLogger(PbcsPlanTypeImpl.class);

	/**
	 * The default import options used by {@link PbcsPlanType#setCell(java.util.List, String)} and similar
	 * convenience methods.
	 */
	public static final ImportDataOptions DEFAULT_IMPORT_OPTIONS = new ImportDataOptionsImpl();

	private final PbcsApplication application;

	private final String planType;

	private final PbcsApplication.PlanTypeConfiguration configuration;

	/**
	 * The member dimension cache configured for this plan type.
	 */
	protected final MemberDimensionCache memberDimensionCache;

	/**
	 * The member resolver configured for this plan type.
	 */
	protected final MemberResolver memberResolver;

	PbcsPlanTypeImpl(RestContext context, PbcsApplication application, PbcsApplication.PlanTypeConfiguration configuration) {
		super(context);
		this.application = application;
		this.planType = configuration.getName();
		this.configuration = configuration;
		this.memberDimensionCache = configuration.getMemberDimensionCache();
		this.memberResolver = configuration.getMemberResolver();
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
		return buildDiscoveredDimensions(fetchPlanTypeDimensions(), false);
	}

	/**
	 * Calls the official, plan-type-scoped dimension list endpoint
	 * ({@code applications/{application}/plantypes/{planType}/dimensions}) and returns the raw
	 * deserialized dimension entries.
	 *
	 * @return the list of plan-type dimension entries, empty list if none were returned
	 */
	protected List<PlanTypeDimension> fetchPlanTypeDimensions() {
		PlanTypeDimensionsWrapper wrapper = get("applications/{application}/plantypes/{planType}/dimensions",
				PlanTypeDimensionsWrapper.class, application.getName(), planType);
		return wrapper.getItems() != null ? wrapper.getItems() : new ArrayList<>();
	}

	/**
	 * Builds a list of {@link PbcsDimension} objects from already-deserialized plan-type dimension entries,
	 * as returned by {@link #fetchPlanTypeDimensions()}, classifying each dimension's type from its
	 * {@link PlanTypeDimension#getDimType()} value and, if requested, validating each dimension using the
	 * response's own {@link PlanTypeDimension#isValid()} flag rather than issuing per-dimension REST calls.
	 *
	 * @param dimensions the deserialized dimension entries, in the order returned by the endpoint
	 * @param validateDimensions if true, the first entry with {@code valid == false} causes a
	 *                           {@link PbcsInvalidDimensionException} to be thrown for that dimension name
	 * @return the built dimension objects, numbered in the order provided
	 * @throws PbcsInvalidDimensionException if validateDimensions is true and a dimension has valid == false
	 */
	protected List<PbcsDimension> buildDiscoveredDimensions(List<PlanTypeDimension> dimensions, boolean validateDimensions) {
		List<PbcsDimension> result = new ArrayList<>();
		int dimNumber = 0;
		for (PlanTypeDimension dimension : dimensions) {
			String name = dimension.getDimensionName();
			if (validateDimensions && !dimension.isValid()) {
				throw new PbcsInvalidDimensionException(name);
			}
			result.add(new ExplicitDimension(name, dimNumber++, PbcsMemberType.fromDimType(dimension.getDimType())));
		}
		return result;
	}

	@Override
	public List<PbcsJobDefinition> getJobs() {
		return application.getJobDefinitions().stream()
				.filter(job -> planType.equals(job.getPlanTypeName()))
				.collect(Collectors.toList());
	}

    @Override
    public List<PbcsJobDefinition> getJobs(PbcsJobType jobType) {
        return getJobs().stream()
                .filter(job -> job.getJobType().equals(jobType))
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
	public PbcsApplication getParent() {
		return getApplication();
	}

	@Override
	public PbcsApplication.PlanTypeConfiguration getConfiguration() {
		return configuration;
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
		GridDefinition gridDefinition = new GridDefinition(dataPoint);
		ExportDataSlice exportDataSlice = new ExportDataSlice(gridDefinition);
        DataSlice dataSlice = post("applications/{application}/plantypes/{planType}/exportdataslice", exportDataSlice, DataSlice.class, application.getName(), planType);
        return new DataSliceGrid(this, dataSlice);
	}

	@Override
	public DataSliceGrid retrieve(List<String> pov, Grid<String> grid) {
		PovGrid<String> povGrid = new PovGridImpl<>(pov, grid);
		DataSlice dataSlice = retrieveToSlice(povGrid);
		return new DataSliceGrid(this, dataSlice);
	}

	/**
	 * Performs a retrieve for the given grid, returning the raw data slice response.
	 *
	 * @param grid the grid to retrieve
	 * @return the resulting data slice
	 */
	protected DataSlice retrieveToSlice(PovGrid<String> grid) {
		try {
			GridDefinition gridDefinition = new GridDefinition(grid);
			ExportDataSlice exportDataSlice = new ExportDataSlice(gridDefinition);
			return post("applications/{application}/plantypes/{planType}/exportdataslice", exportDataSlice, DataSlice.class, application.getName(), planType);
		} catch (Exception e) {
			throw new PbcsDataExportException(grid, e);
		}
	}

	@Override
	public DataSliceGrid retrieve(PovGrid<String> grid, RetrieveOptions options) {
		throw new UnsupportedOperationException("Can only retrieve with options on explicit dimension plan");
	}

	@Override
	public void export(PbcsPov pov, String top, DimensionMembers rows, ExportCallback exportCallback) {
		final int gridRows = rows.getMembers().get(0).size() + 1;
		final int gridColumns = rows.getMembers().size() + 1;
		Grid<String> grid = new HashMapGrid<>(gridRows, gridColumns);
		grid.setCell(0, gridColumns - 1, top);

		int colOffset = 0;
		for (List<String> column : rows.getMembers()) {
			GridDrawing.drawColumn(grid, 1, colOffset++, column);
		}

		PovGrid<String> povGrid = new PovGridImpl<>(pov.memberNames(), grid);
		DataSlice slice = retrieveToSlice(povGrid);

		exportCallback.pov(pov);
		exportCallback.printHeaders(rows.getDimensions(), slice.getColumns().get(0));

		for (DataSlice.HeaderDataRow row : slice.getRows()) {
			List<PbcsMember> members = new ArrayList<>();
			for (String header : row.getHeaders()) {
				PbcsMember member = getMember(header);
				members.add(member);
			}
			exportCallback.printRow(members, row.getData());
		}
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
		DataSlice dataSlice = createDataSlice(pov, values, importDataOptions);
		importDataSlice.setDataGrid(dataSlice);

		logger.info("Updating {}.{} at POV {} using a {}x{} source grid", application.getName(), planType, pov, values.getRows(), values.getColumns());

		PovGrid<String> povGrid = new PovGridImpl<>(pov, values);
		DataSlice beforeSlice = importDataOptions.isReturnChangedCells() ? retrieveToSlice(povGrid) : null;
		ImportDataResultImpl importDataResult = importDataSlice(importDataSlice, importDataOptions);

		if (importDataOptions.isReturnChangedCells()) {
			DataSlice afterSlice = retrieveToSlice(povGrid);
			Map<Set<String>, DataSliceDiff.ValChange> changes = DataSliceDiff.diff(beforeSlice, afterSlice);
			importDataResult.setChanges(changes);
		}

		return importDataResult;
	}

	private DataSlice createDataSlice(List<String> pov, Grid<String> grid, ImportDataOptions importDataOptions) {
		int firstRowWithCell = GridUtils.firstNonNullInColumn(grid, 0);
		int firstColWithCell = GridUtils.firstNonNullInRow(grid, 0);

		List<List<String>> columns = new ArrayList<>();
		for (int row = 0; row < firstRowWithCell; row++) {
			List<String> column = new ArrayList<>();
			for (int col = firstColWithCell; col < grid.getColumns(); col++) {
				column.add(grid.getCell(row, col));
			}
			columns.add(column);
		}

		List<DataSlice.HeaderDataRow> rows = new ArrayList<>();
		for (int row = firstRowWithCell; row < grid.getRows(); row++) {
			List<String> headers = new ArrayList<>();
			for (int col = 0; col < firstColWithCell; col++) {
				headers.add(grid.getCell(row, col));
			}
			List<String> data = new ArrayList<>();
			for (int col = firstColWithCell; col < grid.getColumns(); col++) {
				String dataCell = grid.getCell(row, col);
				if (importDataOptions.isTreatZerosAsMissing() && NumberUtil.isNumeric(dataCell) && Double.parseDouble(dataCell) == 0) {
					dataCell = PbcsPlanType.IMPORT_MISSING;
				} else if (importDataOptions.isTreatBlankAsMissing() && !StringUtils.hasText(dataCell)) {
					dataCell = PbcsPlanType.IMPORT_MISSING;
				}
				data.add(dataCell);
			}
			DataSlice.HeaderDataRow headerDataRow = new DataSlice.HeaderDataRow(headers, data);
			rows.add(headerDataRow);
		}
		return new DataSlice(pov, columns, rows);
	}

	private ImportDataResultImpl importDataSlice(ImportDataSlice importDataSlice, ImportDataOptions importDataOptions) {

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
	public PbcsMember getMember(String dimensionName, String memberName) {
		PbcsMember member = application.getMember(dimensionName, memberName);
		if (member instanceof PbcsMemberImpl memberImpl) {
			return memberImpl.forPlanType(this);
		}
		return member;
	}

	// TODO: refactor to go through member resolver or similar codepath to getMemberOrAlias
	@Override
	public PbcsMember getMember(String memberName) {
		String dimensionName = findMemberDimensionFromCache(memberName);
		if (dimensionName != null) {
			return getMember(dimensionName, memberName);
		} else {
			throw new PbcsClientException("Unable to determine dimension for member (try using explicit dimensions plan type)" + memberName);
		}
	}

	@Override
	public List<PbcsMember> queryMembers(String memberName, PbcsMemberQueryType queryType) {
		PbcsMember member = getMemberOrAlias(memberName);
		if (member == null) throw new PbcsNoSuchObjectException(memberName, PbcsObjectType.MEMBER);

		List<PbcsMember> results = new ArrayList<>();

		switch (queryType) {
			case ICHILDREN:
				results.add(member);
			case CHILDREN:
				for (PbcsMember child : member.getChildren()) {
					results.add(child);
				}
				break;
			case IDESCENDANTS:
				results.add(member);
			case DESCENDANTS:
				// do first iteration ourselves here so that resulting list doesn't include root member
				for (PbcsMember child : member.getChildren()) {
					processChildren(results, child);
				}
				break;
			case IANCESTORS:
				results.add(member);
			case ANCESTORS:
				while (member.getParentName() != null) {
					PbcsMember parent = getMember(member.getDimensionName(), member.getParentName());
					results.add(parent);
					member = parent;
				}
				break;
			case ISIBLINGS:
			case SIBLINGS:
				PbcsMember parent = getMember(member.getDimensionName(), member.getParentName());
				if (queryType.isIncludeOriginalMember()) {
					results.addAll(parent.getChildren());
				} else {
					for (PbcsMember sibling : parent.getChildren()) {
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
	public List<PbcsMember> searchMembers(MemberSearchQuery query) {
		throw new UnsupportedOperationException();
	}

	private static void processChildren(List<PbcsMember> members, PbcsMember currentMember) {
		members.add(currentMember);
		for (PbcsMember child : currentMember.getChildren()) {
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
	public PbcsMember getMemberOrAlias(String memberOrAliasName) {
		throw new IllegalStateException("Must configure explicit dimensions to search for alias");
	}

	/**
	 * Looks up the dimension name for the given member using the configured member dimension cache.
	 *
	 * @param memberName the member name
	 * @return the dimension name, or null if not resolvable from the cache
	 */
	public String findMemberDimensionFromCache(String memberName) {
		String dimensionName = memberDimensionCache.getDimensionName(this, memberName);
		if (dimensionName == null) {
			logger.warn("Tried to find dimension for member {} but this is not an explicit dimensions plan type and the member-dimension cache did not resolve the dimension", memberName);
		} else {
			logger.trace("Member {} has dimension {} from cache", memberName, dimensionName);
		}
		return dimensionName;
	}

	/**
	 * A {@link PbcsDimension} identified explicitly by name, number, and type, whether that identity came
	 * from a caller-supplied explicit dimension list or was discovered via a dimension list REST endpoint.
	 */
	protected class ExplicitDimension extends AbstractPbcsObject implements PbcsDimension {

		private final String name;

		private final int number;

		private final PbcsMemberType type;

		/**
		 * Constructs an instance for the given dimension identity.
		 *
		 * @param name the dimension name
		 * @param number the dimension's number within its plan type
		 * @param type the dimension's type
		 */
		protected ExplicitDimension(String name, int number, PbcsMemberType type) {
			super(PbcsPlanTypeImpl.this.context);
			this.name = name;
			this.number = number;
			this.type = type;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public PbcsObjectType getObjectType() {
			return PbcsObjectType.DIMENSION;
		}

		@Override
		public int getNumber() {
			return number;
		}

		@Override
		public PbcsMember getMember(String memberName) {
			return PbcsPlanTypeImpl.this.getMember(name, memberName);
		}

		@Override
		public PbcsMemberType getDimensionType() {
			return type;
		}

		@Override
		public PbcsApplication getParent() {
			return getApplication();
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

		@Override
		public String toString() {
			return name;
		}

	}

	private static class ImportDataResultImpl implements ImportDataResult {

		private final ImportDataSliceResponse response;

		private Map<Set<String>, DataSliceDiff.ValChange> changes;

		public ImportDataResultImpl(ImportDataSliceResponse response) {
			this.response = response;
		}

		public int getAcceptedCells() {
			return response.getNumAcceptedCells();
		}

		public int getRejectedCells() {
			return response.getNumRejectedCells();
		}

		public Map<Set<String>, DataSliceDiff.ValChange> getChanges() {
			return changes;
		}

		public void setChanges(Map<Set<String>, DataSliceDiff.ValChange> changes) {
			this.changes = changes;
		}

	}

	/**
	 * Default, mutable {@link ImportDataOptions} implementation.
	 */
	public static class ImportDataOptionsImpl implements ImportDataOptions {

		private boolean aggregateData;

		private CellNotesOption cellNotesOption = CellNotesOption.SKIP;

		private String dateFormat = "DD/MM/YYYY";

		private boolean strictDateValidation = true;

		private boolean dryRun;

		private boolean includeRejectedCells = true;

		private boolean includeRejectedCellsWithDetails = true;

		private String postDataImportRuleNames;

		private boolean throwExceptionIfAnyRejectedCells;

		private boolean returnChangedCells;

		private boolean treatZerosAsMissing = false;

		private boolean treatBlankAsMissing = false;

		/**
		 * Constructs an instance with default options.
		 */
		public ImportDataOptionsImpl() {
		}

		@Override
		public boolean isAggregateData() {
			return aggregateData;
		}

		/**
		 * Sets whether values should be added to existing values.
		 *
		 * @param aggregateData true to aggregate, false otherwise
		 */
		public void setAggregateData(boolean aggregateData) {
			this.aggregateData = aggregateData;
		}

		@Override
		public CellNotesOption getCellNotesOption() {
			return cellNotesOption;
		}

		/**
		 * Sets the cell notes option.
		 *
		 * @param cellNotesOption the cell notes option
		 */
		public void setCellNotesOption(CellNotesOption cellNotesOption) {
			this.cellNotesOption = cellNotesOption;
		}

		@Override
		public String getDateFormat() {
			return dateFormat;
		}

		/**
		 * Sets the date format used to parse date-typed cell values.
		 *
		 * @param dateFormat the date format
		 */
		public void setDateFormat(String dateFormat) {
			this.dateFormat = dateFormat;
		}

		@Override
		public boolean isStrictDateValidation() {
			return strictDateValidation;
		}

		/**
		 * Sets whether strict date validation is enabled.
		 *
		 * @param strictDateValidation true to enable strict date validation, false otherwise
		 */
		public void setStrictDateValidation(boolean strictDateValidation) {
			this.strictDateValidation = strictDateValidation;
		}

		@Override
		public boolean isDryRun() {
			return dryRun;
		}

		/**
		 * Sets whether this is a dry run.
		 *
		 * @param dryRun true if a dry run, false otherwise
		 */
		public void setDryRun(boolean dryRun) {
			this.dryRun = dryRun;
		}

		@Override
		public boolean isIncludeRejectedCells() {
			return includeRejectedCells;
		}

		/**
		 * Sets whether rejected cells should be included in the response.
		 *
		 * @param includeRejectedCells true to include rejected cells, false otherwise
		 */
		public void setIncludeRejectedCells(boolean includeRejectedCells) {
			this.includeRejectedCells = includeRejectedCells;
		}

		@Override
		public boolean isIncludeRejectedCellsWithDetails() {
			return includeRejectedCellsWithDetails;
		}

		/**
		 * Sets whether rejected cells should include additional details.
		 *
		 * @param includeRejectedCellsWithDetails true to include additional details, false otherwise
		 */
		public void setIncludeRejectedCellsWithDetails(boolean includeRejectedCellsWithDetails) {
			this.includeRejectedCellsWithDetails = includeRejectedCellsWithDetails;
		}

		@Override
		public String getPostDataImportRuleNames() {
			return postDataImportRuleNames;
		}

		/**
		 * Sets the post data import rule names.
		 *
		 * @param postDataImportRuleNames the post data import rule names
		 */
		public void setPostDataImportRuleNames(String postDataImportRuleNames) {
			this.postDataImportRuleNames = postDataImportRuleNames;
		}

		@Override
		public boolean isThrowExceptionIfAnyRejectedCells() {
			return throwExceptionIfAnyRejectedCells;
		}

		/**
		 * Sets whether an exception should be thrown if any cells are rejected.
		 *
		 * @param throwExceptionIfAnyRejectedCells true to throw on rejected cells, false otherwise
		 */
		public void setThrowExceptionIfAnyRejectedCells(boolean throwExceptionIfAnyRejectedCells) {
			this.throwExceptionIfAnyRejectedCells = throwExceptionIfAnyRejectedCells;
		}

		@Override
		public boolean isReturnChangedCells() {
			return returnChangedCells;
		}

		/**
		 * Sets whether updated cells should be returned.
		 *
		 * @param returnChangedCells true to return updated cells, false otherwise
		 */
		public void setReturnChangedCells(boolean returnChangedCells) {
			this.returnChangedCells = returnChangedCells;
		}

		@Override
		public boolean isTreatBlankAsMissing() {
			return treatBlankAsMissing;
		}

		/**
		 * Sets whether blank values should be treated as missing.
		 *
		 * @param treatBlankAsMissing true to treat blanks as missing, false otherwise
		 */
		public void setTreatBlankAsMissing(boolean treatBlankAsMissing) {
			this.treatBlankAsMissing = treatBlankAsMissing;
		}

		@Override
		public boolean isTreatZerosAsMissing() {
			return treatZerosAsMissing;
		}

		/**
		 * Sets whether zero values should be treated as missing.
		 *
		 * @param treatZerosAsMissing true to treat zeros as missing, false otherwise
		 */
		public void setTreatZerosAsMissing(boolean treatZerosAsMissing) {
			this.treatZerosAsMissing = treatZerosAsMissing;
		}

	}

}
