package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.api.v3.AliasedMember;
import com.jasonwjones.pbcs.api.v3.PlanTypeDimension;
import com.jasonwjones.pbcs.api.v3.PlanTypeEntry;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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

	private PlanTypeEntry details;

	/**
	 * Records the listing entry this plan type was built from.
	 *
	 * <p>Set after construction rather than passed in, so that the subclass constructor chain does not
	 * have to carry a value only one of the two creation paths ever has: a plan type opened by name was
	 * never listed and has none.
	 *
	 * @param details the entry from the plan type list endpoint
	 */
	void setDetails(PlanTypeEntry details) {
		this.details = details;
	}

	/**
	 * Why this plan type cannot do something, and what to do about it.
	 *
	 * <p>These used to say the plan had no explicit dimensions, which is true and is almost never what
	 * the caller needs to hear: explicit dimensions are a configuration knob, and the usual cause is
	 * having taken a plan out of {@link PbcsApplication#getPlanTypes()}, which returns plans for listing
	 * an application's cubes rather than for working with them. Nothing distinguishes the two at the
	 * call site - same interface, same type - so the message is the only place a caller can find out.
	 *
	 * @param what the operation that cannot be performed, as a verb phrase
	 * @return the message
	 */
	private String cannotWithoutDimensions(String what) {
		return "Cannot " + what + " on plan type " + planType + ": it has no dimensions to work from."
				+ " Plans from getPlanTypes() are for listing an application's cubes; get one that can"
				+ " do this with application.getPlanType(\"" + planType + "\"), or configure explicit"
				+ " dimensions with getPlanType(PlanTypeConfiguration).";
	}

	@Override
	public PlanTypeEntry getDetails() {
		return details;
	}

	private volatile List<PbcsDimension> dimensions;

	/**
	 * {@inheritDoc}
	 *
	 * <p>Fetched once and kept. This used to call the endpoint on every invocation, which is affordable
	 * for a caller asking once and ruinous for anything asking per member - and asking per member is
	 * what resolving a member or an alias does. A plan type's dimensions do not change under a running
	 * application often enough to be worth a round trip each time; a caller who needs to see an outline
	 * change opens the plan again.
	 */
	@Override
	public List<PbcsDimension> getDimensions() {
		List<PbcsDimension> cached = dimensions;
		if (cached == null) {
			// Two callers racing here both fetch and the second wins, which costs one extra call in a
			// case that barely happens and avoids holding a lock across a REST request.
			cached = buildDiscoveredDimensions(fetchPlanTypeDimensions(), false);
			dimensions = cached;
		}
		return cached;
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
	public Map<String, String> getMemberAliases(String dimensionName, String aliasTableName) {
		if (!StringUtils.hasText(dimensionName)) {
			throw new IllegalArgumentException("Must specify a dimension name");
		}
		Map<String, String> aliases = flattenMemberAliases(fetchMemberAliasTree(dimensionName, aliasTableName));
		for (Map.Entry<String, String> entry : aliases.entrySet()) {
			memberResolver.setAlias(this, entry.getKey(), aliasTableName, entry.getValue());
		}
		aliasTrees.put(aliasTreeKey(dimensionName, aliasTableName), aliases);
		// The reverse index is derived from this, so a re-read invalidates it rather than leaving a
		// stale one that disagrees with the map it was built from.
		membersByAlias.remove(aliasTreeKey(dimensionName, aliasTableName));
		return aliases;
	}

	/**
	 * Alias tables already read, whole, by dimension.
	 *
	 * <p>Aliases arrive a dimension at a time, so the answer for one member is a lookup in a map that
	 * was already downloaded to answer it. Kept here rather than inferred from the resolver because a
	 * resolver answers null both for a member it has not seen and for one it has seen to have no alias,
	 * and those need telling apart: without that, every member without an alias re-downloaded its whole
	 * dimension on every ask - which, in a grid, is most of them, most of the time. Reading it from the
	 * resolver instead would also make correctness depend on which resolver is configured, and one that
	 * deliberately keeps nothing would turn every alias into null.
	 */
	private final ConcurrentMap<String, Map<String, String>> aliasTrees = new ConcurrentHashMap<>();

	/**
	 * The same tables read the other way round: alias to member name, per dimension and table.
	 *
	 * <p>Needed because the two directions are asked for by different things. Rendering a grid asks
	 * for a member's alias, which the forward map answers. Retrieving one asks the opposite - here is
	 * a name off a sheet, which member is it - and only the Default table could answer that, because
	 * the only reverse lookup there was is {@link PbcsMember#searchForDescendant(String)} comparing
	 * {@code getAlias()}, and that is the Default alias by definition.
	 *
	 * <p>Derived from the forward map rather than fetched, so it costs no extra request: the tree it
	 * inverts was downloaded to answer the forward question anyway.
	 */
	private final ConcurrentMap<String, Map<String, String>> membersByAlias = new ConcurrentHashMap<>();

	/**
	 * Which member an alias belongs to, for one dimension and one alias table.
	 *
	 * <p>Aliases are unique per table in a well-formed outline, but not always in a real one - an FCCS
	 * Currency dimension is the standing example - so a repeat is kept as the first member that
	 * claimed it and logged rather than silently overwriting. Picking the last one would make which
	 * member an alias resolves to depend on dimension ordering, which is nobody's intent.
	 *
	 * @param dimensionName  the dimension whose aliases to read
	 * @param aliasTableName the alias table; null, blank, and {@code Default} are the same table
	 * @return alias to member name, empty if the table gives this dimension no aliases
	 */
	protected Map<String, String> getMembersByAlias(String dimensionName, String aliasTableName) {
		String key = aliasTreeKey(dimensionName, aliasTableName);
		Map<String, String> known = membersByAlias.get(key);
		if (known != null) {
			return known;
		}
		// Built outside a computeIfAbsent, because fetching the forward map invalidates this one - and
		// a mapping function that touches the map it is populating is a "Recursive update" from
		// ConcurrentHashMap, not a deadlock-free reentrancy. Two threads racing here both build the
		// same answer from the same tree, so the last one winning costs nothing.
		Map<String, String> forward = aliasTrees.get(key);
		if (forward == null) {
			forward = getMemberAliases(dimensionName, aliasTableName);
		}
		Map<String, String> reverse = new LinkedHashMap<>();
		for (Map.Entry<String, String> entry : forward.entrySet()) {
			String existing = reverse.putIfAbsent(entry.getValue(), entry.getKey());
			if (existing != null && !existing.equals(entry.getKey())) {
				logger.warn("Alias {} in table {} belongs to both {} and {} in dimension {}; keeping {}",
						entry.getValue(), aliasTableName, existing, entry.getKey(), dimensionName, existing);
			}
		}
		membersByAlias.put(key, reverse);
		return reverse;
	}

	private static String aliasTreeKey(String dimensionName, String aliasTableName) {
		return dimensionName + '@'
				+ (PbcsMember.isDefaultAliasTable(aliasTableName) ? "Default" : aliasTableName);
	}

	@Override
	public String getMemberAlias(String dimensionName, String memberName, String aliasTableName) {
		String cached = memberResolver.getAlias(this, memberName, aliasTableName);
		if (cached != null) {
			return cached;
		}
		Map<String, String> tree = aliasTrees.get(aliasTreeKey(dimensionName, aliasTableName));
		if (tree == null) {
			tree = getMemberAliases(dimensionName, aliasTableName);
		}
		return tree.get(memberName);
	}

	/**
	 * Calls the official Get Dimension Details REST endpoint
	 * ({@code applications/{application}/plantypes/{planType}/dimensions/{dimensionName}}) requesting only
	 * the name, alias, and children fields, and returns the raw deserialized member tree (the dimension's
	 * root member).
	 *
	 * @param dimensionName the dimension name
	 * @param aliasTableName the alias table to resolve aliases from, or null/blank to use the server's
	 *                       default (the Default table)
	 * @return the root of the dimension's member tree, with aliases resolved from the given table
	 */
	protected AliasedMember fetchMemberAliasTree(String dimensionName, String aliasTableName) {
		if (StringUtils.hasText(aliasTableName)) {
			return get("applications/{application}/plantypes/{planType}/dimensions/{dimensionName}?aliasTableName={aliasTableName}&fields=name,alias,children",
					AliasedMember.class, application.getName(), planType, dimensionName, aliasTableName);
		} else {
			return get("applications/{application}/plantypes/{planType}/dimensions/{dimensionName}?fields=name,alias,children",
					AliasedMember.class, application.getName(), planType, dimensionName);
		}
	}

	/**
	 * Flattens an already-deserialized member alias tree, as returned by {@link #fetchMemberAliasTree(String, String)},
	 * into a map of member name to alias, omitting members that have no alias in the requested table or
	 * whose alias is identical to their name.
	 *
	 * @param root the root of the member alias tree
	 * @return the flattened member name to alias map
	 */
	protected Map<String, String> flattenMemberAliases(AliasedMember root) {
		Map<String, String> aliases = new LinkedHashMap<>();
		collectMemberAliases(root, aliases);
		return aliases;
	}

	private static void collectMemberAliases(AliasedMember member, Map<String, String> aliases) {
		String alias = member.getAlias();
		if (StringUtils.hasText(alias) && !alias.equals(member.getName())) {
			aliases.put(member.getName(), alias);
		}
		if (member.getChildren() != null) {
			for (AliasedMember child : member.getChildren()) {
				collectMemberAliases(child, aliases);
			}
		}
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
		throw new UnsupportedOperationException(cannotWithoutDimensions("get the default cell"));
	}

	@Override
	public String getCell(List<String> dataPoint) {
		dataPoint = canonicalMemberNames(dataPoint);
		// just lean on the implementation available in the application to avoid duplication
		DataSlice dataSlice = this.application.exportDataSlice(getName(), new ExportDataSlice(new GridDefinition(dataPoint)));
		DataSlice.HeaderDataRow headerDataRow = dataSlice.getRows().get(0);
		return headerDataRow.getData().get(0);
	}

	@Override
	public DataSliceGrid retrieve() {
		throw new UnsupportedOperationException(cannotWithoutDimensions("retrieve the default grid"));
	}

	@Override
	public DataSliceGrid retrieve(List<String> dataPoint) {
		GridDefinition gridDefinition = new GridDefinition(canonicalMemberNames(dataPoint));
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
		throw new UnsupportedOperationException(cannotWithoutDimensions("retrieve"));
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
				// getMemberOrAlias, because a header is whichever of the two the retrieve happened to
				// return: a grid fetched with aliases on names its rows by alias. getMember means "by
				// member name" and says so, and asking it for something else got an exception naming a
				// member that plainly exists. The heavier lookup belongs here, at the one call that
				// cannot know which it is holding, rather than inside a method whose name is a promise.
				PbcsMember member = getMemberOrAlias(header);
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
		// Aliases resolved to member names first, as getCell and retrieve already do. Without it a read
		// took an alias and the matching write did not - and not by failing: importdataslice accepts a
		// cell it cannot place, reports it accepted, rejects nothing, and stores nothing. A write that
		// reports success and does not happen is the worst shape a bug can take.
		pov = canonicalMemberNames(pov, true);
		value = importValue(value, importDataOptions);
		String before = importDataOptions.isReturnChangedCells() ? getCell(pov) : null;
		ImportDataSlice importDataSlice = new ImportDataSlice(pov, value);
		logger.info("Updating {}.{} to set cell {} to {}", application.getName(), planType, pov, value);
		ImportDataResultImpl result = importDataSlice(importDataSlice, importDataOptions);
		if (importDataOptions.isReturnChangedCells()) {
			result.setChanges(changedCell(pov, before));
		}
		return result;
	}

	@Override
	public List<String> getCellNotes(List<String> dataPoint) {
		DataSliceGrid.DataCell cell = planningDataCell(dataPoint);
		return cell == null ? Collections.emptyList() : cell.getCellNotes();
	}

	@Override
	public List<DataSlice.SupportingDetail> getSupportingDetail(List<String> dataPoint) {
		DataSliceGrid.DataCell cell = planningDataCell(dataPoint);
		return cell == null ? Collections.emptyList() : cell.getSupportingDetail();
	}

	/** Exports a single cell with its planning data (notes and supporting detail), or null if none came back. */
	private DataSliceGrid.DataCell planningDataCell(List<String> dataPoint) {
		dataPoint = canonicalMemberNames(dataPoint);
		ExportDataSlice exportDataSlice = new ExportDataSlice(new GridDefinition(dataPoint));
		exportDataSlice.setExportPlanningData(true);
		DataSlice dataSlice = post("applications/{application}/plantypes/{planType}/exportdataslice", exportDataSlice, DataSlice.class, application.getName(), planType);
		if (dataSlice.getRows().isEmpty()) {
			return null;
		}
		DataSliceGrid grid = new DataSliceGrid(this, dataSlice);
		return (DataSliceGrid.DataCell) grid.getCell(grid.getTopRows(), grid.getLeftCols());
	}

	@Override
	public List<DataSlice.SupportingDetail> setSupportingDetail(List<String> dataPoint, List<DataSlice.SupportingDetail> supportingDetail) {
		// Validated and totalled before anything is sent: the server reports a bad operator as malformed
		// JSON, and stores a value that does not match the lines without a word.
		String value = "";
		List<DataSlice.SupportingDetail> lines = Collections.emptyList();
		if (!supportingDetail.isEmpty()) {
			SupportingDetailCalculator calculation = SupportingDetailCalculator.calculate(supportingDetail);
			if (calculation.getTotal() == null) {
				throw new IllegalArgumentException("Supporting detail adds up to nothing, so there is no value to store it with: " + supportingDetail.size() + " line(s) and none contributes a value");
			}
			value = calculation.getTotal();
			lines = calculation.getLines();
		}
		// As setCell: an alias the server cannot place is accepted and quietly not written.
		dataPoint = canonicalMemberNames(dataPoint, true);

		// An empty wrapper deletes the cell's supporting detail, and with it the blank value leaves the cell's
		// value as it was. Lines are only stored beside a non-blank value, which is why the total is sent.
		ImportDataSlice importDataSlice = new ImportDataSlice(dataPoint, value);
		importDataSlice.getDataGrid().getRows().get(0).setSupportingDetail(Collections.singletonList(new DataSlice.SupportingDetailWrapper(lines)));
		// Unlike a note, supporting detail on a cell that cannot take it is rejected rather than dropped, so
		// the rejection is worth an exception.
		ImportDataOptionsImpl importDataOptions = new ImportDataOptionsImpl();
		importDataOptions.setThrowExceptionIfAnyRejectedCells(true);

		logger.info("Updating {}.{} to set {} line(s) of supporting detail totalling {} at {}", application.getName(), planType, lines.size(), value.isEmpty() ? "(unchanged)" : value, dataPoint);
		importDataSlice(importDataSlice, importDataOptions);
		return getSupportingDetail(dataPoint);
	}

	@Override
	public List<String> setCellNotes(List<String> dataPoint, List<String> notes, CellNotesOption cellNotesOption) {
		if (cellNotesOption == CellNotesOption.SKIP) {
			throw new IllegalArgumentException("Writing cell notes with " + cellNotesOption + " would write nothing");
		}
		// As setCell: an alias the server cannot place is accepted and quietly not written.
		dataPoint = canonicalMemberNames(dataPoint, true);
		List<DataSlice.CellNote> cellNotes = new ArrayList<>(notes.size());
		for (String note : notes) {
			cellNotes.add(new DataSlice.CellNote(note));
		}

		// The blank value is what leaves the cell's value alone; see importValue.
		ImportDataSlice importDataSlice = new ImportDataSlice(dataPoint, "");
		importDataSlice.getDataGrid().getRows().get(0).setCellNotes(Collections.singletonList(cellNotes));
		ImportDataOptionsImpl importDataOptions = new ImportDataOptionsImpl();
		importDataOptions.setCellNotesOption(cellNotesOption);

		logger.info("Updating {}.{} to {} {} cell note(s) at {}", application.getName(), planType, cellNotesOption, notes.size(), dataPoint);
		importDataSlice(importDataSlice, importDataOptions);
		// Read back because nothing else can say whether the notes landed: the import's counts cover values
		// only, and a note on a cell that will not take one is dropped without being rejected.
		return getCellNotes(dataPoint);
	}

	@Override
	public ImportDataResult setCells(List<String> pov, Grid<String> values) {
		return setCells(pov, values, DEFAULT_IMPORT_OPTIONS);
	}

	@Override
	public ImportDataResult setCells(List<String> pov, Grid<String> values, ImportDataOptions importDataOptions) {
		// As setCell: an alias anywhere in the POV is accepted and quietly not written.
		pov = canonicalMemberNames(pov, true);
		ImportDataSlice importDataSlice = new ImportDataSlice();
		DataSlice dataSlice = createDataSlice(pov, values, importDataOptions);
		importDataSlice.setDataGrid(dataSlice);

		logger.info("Updating {}.{} at POV {} using a {}x{} source grid", application.getName(), planType, pov, values.getRows(), values.getColumns());

		PovGrid<String> povGrid = new PovGridImpl<>(pov, values);
		DataSlice beforeSlice = importDataOptions.isReturnChangedCells() ? retrieveToSlice(povGrid) : null;
		ImportDataResultImpl importDataResult = importDataSlice(importDataSlice, importDataOptions);

		if (importDataOptions.isReturnChangedCells()) {
			// As in changedCell: the write is done, so a failure to read it back is not a failure of
			// the write, and throwing here would say otherwise.
			try {
				DataSlice afterSlice = retrieveToSlice(povGrid);
				importDataResult.setChanges(DataSliceDiff.diff(beforeSlice, afterSlice));
			} catch (RuntimeException couldNotReadBack) {
				logger.warn("Wrote the grid at {} but could not read it back to say what changed; the"
						+ " write itself succeeded", pov, couldNotReadBack);
			}
		}

		return importDataResult;
	}

	/**
	 * What the cell holds now, against what it held before, for a write that asked to be checked.
	 *
	 * <p>Read back rather than inferred, because the counts cannot answer it: importdataslice reports
	 * a cell accepted that it never stored, and numUpdateCells is not returned by every pod. Reading
	 * the cell is the only answer that is about the cube rather than about the request.
	 *
	 * <p>A failure here is not a failure of the write. The write has already happened by this point,
	 * so letting the read throw would report a successful write as a broken one - the worst way to be
	 * wrong about it. The change set is advisory; the write is not.
	 *
	 * @param pov the cell, in canonical member names
	 * @param before what it held before the write
	 * @return the one change, or empty if nothing moved or it could not be read back
	 */
	private Map<Set<String>, DataSliceDiff.ValChange> changedCell(List<String> pov, String before) {
		String after;
		try {
			after = getCell(pov);
		} catch (RuntimeException couldNotReadBack) {
			logger.warn("Wrote {} but could not read it back to say what changed; the write itself"
					+ " succeeded", pov, couldNotReadBack);
			return Collections.emptyMap();
		}
		if (Objects.equals(before, after)) {
			return Collections.emptyMap();
		}
		return Collections.singletonMap(new HashSet<>(pov), new DataSliceDiff.ValChange(before, after));
	}

	/**
	 * A cell value as the import should carry it, honouring the options that say what counts as empty.
	 *
	 * <p>setCells has always applied these while building its grid and setCell went straight past
	 * them, so the same option worked through one write method and was ignored by the other. That is
	 * how a cleared cell came to be a silent no-op: EPM reads a blank as "leave it alone", and only
	 * the word #Missing empties it.
	 *
	 * @param value the value as the caller gave it
	 * @param importDataOptions the options for this import
	 * @return the value to send
	 */
	private String importValue(String value, ImportDataOptions importDataOptions) {
		if (importDataOptions.isTreatZerosAsMissing() && NumberUtil.isNumeric(value) && Double.parseDouble(value) == 0) {
			return PbcsPlanType.IMPORT_MISSING;
		}
		if (importDataOptions.isTreatBlankAsMissing() && !StringUtils.hasText(value)) {
			return PbcsPlanType.IMPORT_MISSING;
		}
		return value;
	}

	private DataSlice createDataSlice(List<String> pov, Grid<String> grid, ImportDataOptions importDataOptions) {
		int firstRowWithCell = GridUtils.firstNonNullInColumn(grid, 0);
		int firstColWithCell = GridUtils.firstNonNullInRow(grid, 0);

		List<List<String>> columns = new ArrayList<>();
		for (int row = 0; row < firstRowWithCell; row++) {
			List<String> column = new ArrayList<>();
			for (int col = firstColWithCell; col < grid.getColumns(); col++) {
				column.add(canonicalMemberName(grid.getCell(row, col), true));
			}
			columns.add(column);
		}

		List<DataSlice.HeaderDataRow> rows = new ArrayList<>();
		for (int row = firstRowWithCell; row < grid.getRows(); row++) {
			List<String> headers = new ArrayList<>();
			for (int col = 0; col < firstColWithCell; col++) {
				headers.add(canonicalMemberName(grid.getCell(row, col), true));
			}
			List<String> data = new ArrayList<>();
			for (int col = firstColWithCell; col < grid.getColumns(); col++) {
				data.add(importValue(grid.getCell(row, col), importDataOptions));
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
			logger.info("Update cell result: {} accepted cells, {} updated cells, {} rejected cells",
					importDataSliceResponse.getNumAcceptedCells(),
					importDataSliceResponse.isUpdateCountReported()
							? importDataSliceResponse.getNumUpdateCells() : "(not reported)",
					importDataSliceResponse.getNumRejectedCells());
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
		throw new UnsupportedOperationException(cannotWithoutDimensions("search members"));
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
		throw new IllegalStateException(cannotWithoutDimensions("resolve a member or alias by name"));
	}

	/**
	 * The name to put in a grid for a name that may be an alias the server will not take.
	 *
	 * <p>Established live: Cloud EPM resolves an alias from the <em>Default</em> table itself - a grid
	 * naming {@code Average Salaries} retrieves as happily as one naming {@code 9800} - and rejects an
	 * alias from any other table outright, with "The member X does not exist for the specified cube".
	 * So an alias from a configured table has to be turned back into its member before the request is
	 * built, because there is no asking the server to do it.
	 *
	 * <p>Costs nothing for a plan that was told about no alias tables beyond Default, which is every
	 * plan that has not asked for this: the loop has nothing to iterate and the name is returned as it
	 * came in. For one that has, it is a map lookup after the first, and the maps are the ones the
	 * forward direction downloaded anyway.
	 *
	 * @param name a member name, or an alias from any configured table
	 * @return the member's own name, or {@code name} unchanged if it is not a known alias
	 */
	protected String canonicalMemberName(String name) {
		return canonicalMemberName(name, false);
	}

	/**
	 * As {@link #canonicalMemberName(String)}, optionally resolving the Default table's aliases too.
	 *
	 * <p>Reads leave Default alone because the server resolves it for them: exportdataslice takes a
	 * Default alias and returns the cell. Writes cannot, and this is the asymmetry that matters -
	 * importdataslice takes the same Default alias, reports the cell accepted, rejects nothing, and
	 * stores nothing at all. A write that reports success and does not happen is the worst shape a
	 * bug can take, so the write path pays for the lookup and the read path still does not.
	 *
	 * @param name a member name, or an alias from any configured table
	 * @param includeDefaultTable whether to resolve aliases from the Default table as well
	 * @return the member's own name, or {@code name} unchanged if it is not a known alias
	 */
	protected String canonicalMemberName(String name, boolean includeDefaultTable) {
		if (name == null) {
			return null;
		}
		for (String aliasTable : getAliasTables()) {
			if (!includeDefaultTable && PbcsMember.isDefaultAliasTable(aliasTable)) {
				continue;
			}
			for (PbcsDimension dimension : getDimensions()) {
				String memberName = getMembersByAlias(dimension.getName(), aliasTable).get(name);
				if (memberName != null) {
					logger.debug("Sending {} as {}, its member in alias table {}", name, memberName, aliasTable);
					return memberName;
				}
			}
		}
		return name;
	}

	/** {@link #canonicalMemberName(String)} over a list, returning the same list when nothing changed. */
	protected List<String> canonicalMemberNames(List<String> names) {
		return canonicalMemberNames(names, false);
	}

	/** {@link #canonicalMemberName(String, boolean)} over a list, returning the same list when nothing changed. */
	protected List<String> canonicalMemberNames(List<String> names, boolean includeDefaultTable) {
		if (names == null
				|| (!includeDefaultTable && getAliasTables().stream().allMatch(PbcsMember::isDefaultAliasTable))) {
			return names;
		}
		List<String> canonical = new ArrayList<>(names.size());
		boolean changed = false;
		for (String name : names) {
			String resolved = canonicalMemberName(name, includeDefaultTable);
			changed |= !Objects.equals(resolved, name);
			canonical.add(resolved);
		}
		return changed ? canonical : names;
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
		 * The dimension's root, fetched once.
		 *
		 * <p>Fetching it downloads the whole dimension - the response carries every descendant - and it
		 * is fetched to answer questions that walk that tree: resolving a member searches each
		 * dimension in turn, and each search asked for the root again. So a grid that zoomed three
		 * times paid for the same trees over and over, tens of downloads to place a handful of members
		 * it had already been sent.
		 *
		 * <p>Held for the life of the plan type, like everything else cached here, so an outline edited
		 * underneath one that has already been read will not be noticed - open the plan again for that.
		 */
		private volatile PbcsMember root;

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
		public PbcsMember getRoot() {
			PbcsMember known = root;
			if (known != null) {
				return known;
			}
			synchronized (this) {
				if (root == null) {
					// The one place a dimension is actually fetched, and the expensive thing in
					// resolving a member: the response carries every descendant. Logged here rather
					// than at the cache miss that led to it, because a miss does not imply a request -
					// most are answered from a dimension already in hand. Expect one of these per
					// dimension per plan type, and no more.
					logger.info("Fetching dimension {} of {}", name, PbcsPlanTypeImpl.this.getName());
					root = getMember(name);
				}
				return root;
			}
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

		// Empty rather than null, as the interface says. setCells fills it and setCell never has, so a
		// caller that looped over it worked through one write method and threw through the other.
		private Map<Set<String>, DataSliceDiff.ValChange> changes = Collections.emptyMap();

		public ImportDataResultImpl(ImportDataSliceResponse response) {
			this.response = response;
		}

		public int getAcceptedCells() {
			return response.getNumAcceptedCells();
		}

		public int getUpdatedCells() {
			return response.getNumUpdateCells();
		}

		public boolean isUpdatedCountReported() {
			return response.isUpdateCountReported();
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
