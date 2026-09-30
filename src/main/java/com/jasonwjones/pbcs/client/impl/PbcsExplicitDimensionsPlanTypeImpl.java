package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.api.v3.PlanTypeDimension;
import com.jasonwjones.pbcs.api.v3.dataslices.DataSlice;
import com.jasonwjones.pbcs.api.v3.dataslices.DimensionMembers;
import com.jasonwjones.pbcs.api.v3.dataslices.ExportDataSlice;
import com.jasonwjones.pbcs.api.v3.dataslices.GridDefinition;
import com.jasonwjones.pbcs.client.*;
import com.jasonwjones.pbcs.client.exceptions.*;
import com.jasonwjones.pbcs.client.impl.grid.DataSliceGrid;
import com.jasonwjones.pbcs.client.impl.membervisitors.AbstractMemberVisitor;
import com.jasonwjones.pbcs.client.impl.membervisitors.SearchMemberVisitor;
import com.jasonwjones.pbcs.client.impl.membervisitors.SearchRegexMemberVisitor;
import com.jasonwjones.pbcs.client.impl.membervisitors.SearchWildMemberVisitor;
import com.jasonwjones.pbcs.util.GridUtils;
import com.jasonwjones.pbcs.util.PlanTypeWalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A plan type implementation where the known dimensions are explicitly defined, either by the caller
 * (see {@link PbcsApplication.PlanTypeConfiguration#getExplicitDimensions()}) or discovered automatically
 * via {@link PbcsApplication.PlanTypeConfiguration#isDiscoverDimensions()} or the older
 * {@link PbcsApplication.PlanTypeConfiguration#isQueryDimensions()}. Knowing the dimensions explicitly lets
 * this class significantly enrich functionality beyond what {@link PbcsPlanTypeImpl} alone can offer.
 */
public class PbcsExplicitDimensionsPlanTypeImpl extends PbcsPlanTypeImpl implements PbcsExplicitDimensionsPlanType {

    private static final Logger logger = LoggerFactory.getLogger(PbcsExplicitDimensionsPlanTypeImpl.class);

    private final List<PbcsDimension> explicitDimensions;

    private final ExecutorService executorService;

    /** How many dimensions may be searched at once; see {@link #search}. */
    private final int searchThreads;

    PbcsExplicitDimensionsPlanTypeImpl(RestContext context, PbcsApplication application, PbcsApplication.PlanTypeConfiguration configuration) {
        super(context, application, configuration);

        this.explicitDimensions = new ArrayList<>();
        int dimNumber;

        if (configuration.isDiscoverDimensions()) {
            List<PlanTypeDimension> discovered = fetchPlanTypeDimensions();
            if (discovered.isEmpty()) {
                throw new IllegalArgumentException("Dimension discovery returned no dimensions for plan " + configuration.getName());
            }
            this.explicitDimensions.addAll(buildDiscoveredDimensions(discovered, configuration.isValidateDimensions()));
            dimNumber = this.explicitDimensions.size();
        } else {
            List<String> dimensionNames = new ArrayList<>();
            if (configuration.isQueryDimensions()) {
                for (PbcsDimension dimension : application.getDimensions(configuration.getName())) {
                    dimensionNames.add(dimension.getName());
                }
            } else {
                if (configuration.getExplicitDimensions() == null || configuration.getExplicitDimensions().isEmpty()) throw new IllegalArgumentException("Explicit dimension list cannot be empty");
                dimensionNames.addAll(configuration.getExplicitDimensions());
            }

            if (dimensionNames.isEmpty()) {
                throw new IllegalArgumentException("Dimension name list cannot be empty: provide dimension names or enable query dimensions");
            }

            dimNumber = 0;
            for (String dimName : dimensionNames) {
                PbcsMemberType type = configuration.isValidateDimensions() ?
                        application.getMember(dimName, dimName).getType() :
                        PbcsMemberType.UNKNOWN;
                this.explicitDimensions.add(new ExplicitDimension(dimName, dimNumber++, type));
            }
        }

        processAttributeDimensions(application, configuration, dimNumber);

        logger.debug("{} will use {} thread(s) to perform member name/alias search", this, configuration.getMemberSearchThreads());
        searchThreads = configuration.getMemberSearchThreads();
        executorService = Executors.newFixedThreadPool(searchThreads);
    }

    private void processAttributeDimensions(PbcsApplication application, PbcsApplication.PlanTypeConfiguration configuration, int dimNumber) {
        // add in explicit attribute dimensions, if any, skipping ones already discovered
        if (configuration.getExplicitAttributeDimensions() != null) {
            for (String attribDimName : configuration.getExplicitAttributeDimensions()) {
                if (hasDimension(attribDimName)) {
                    logger.debug("Skipping explicit attribute dimension {} because it was already discovered", attribDimName);
                    continue;
                }
                PbcsMemberType type = configuration.isValidateDimensions() ?
                        application.getMember(attribDimName, attribDimName).getType() :
                        PbcsMemberType.ATTRIBUTE;
                this.explicitDimensions.add(new ExplicitDimension(attribDimName, dimNumber++, type));
            }
        }
    }

    @Override
    public List<PbcsDimension> getDimensions() {
        return explicitDimensions;
    }

    @Override
    public void validateDimensions() {
        for (PbcsDimension explicitDimension : explicitDimensions) {
            explicitDimension.getRoot();
        }
    }

    @Override
    public PbcsDimension getDimension(String dimensionName) {
        for (PbcsDimension dimension : explicitDimensions) {
            if (dimension.getName().equals(dimensionName)) {
                return dimension;
            }
        }
        throw new PbcsInvalidDimensionException(dimensionName);
    }

    @Override
    public boolean isExplicitDimensions() {
        return true;
    }

    @Override
    public PbcsMember getMemberOrAlias(String memberOrAliasName) {
        Objects.requireNonNull(memberOrAliasName, "Must specify a member or alias name");

        try {
            PbcsMember member = memberResolver.getMember(this, memberOrAliasName);
            if (member != null) {
                return member;
            } else {
                // A miss on the member cache, and no more than that. It used to say "from source",
                // which was true when every miss meant a download; now the dimensions are kept, so a
                // miss is usually answered from a tree already in memory and nothing leaves the
                // process. Where a request really does happen is logged where it happens - see
                // ExplicitDimension.getRoot.
                logger.warn("{} is not cached; searching dimensions", memberOrAliasName);
                PbcsMember matchingMember = oneOffSearchInDimension(memberOrAliasName);
                if (matchingMember != null) {
                    // Cached, like every other way of succeeding here. Without this the cheap path was
                    // the one that never got cheaper: a name whose dimension is known walks that
                    // dimension's tree, returns, and is asked for again from scratch on the next call -
                    // forever, logging the miss every time. Anything that had already
                    // been through getMember, which fills the dimension cache and not this one, landed
                    // in exactly that state.
                    memberResolver.setMember(this, memberOrAliasName, matchingMember);
                    return matchingMember;
                }

                List<MemberSearchCallable> searchers;
                if (getDimensionNames().contains(memberOrAliasName)) {
                    // shortcut when the member being queried literally is a dimension
                    PbcsDimension searchDimension = getDimension(memberOrAliasName);
                    searchers = Collections.singletonList(new MemberSearchCallable(searchDimension, memberOrAliasName));
                } else {
                    // you can technically re-search a dimension, but that only happens when you have a bad cache
                    searchers = explicitDimensions.stream()
                            .map(dimension -> new MemberSearchCallable(dimension, memberOrAliasName))
                            .toList();
                }

                try {
                    member = search(searchers);
                    logger.debug("Found member {} (via {}) in dimension {}", member.getName(), memberOrAliasName, member.getDimensionName());
                    memberDimensionCache.setDimension(this, memberOrAliasName, member.getDimensionName());
                    memberResolver.setMember(this, memberOrAliasName, member);
                    return member;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    logger.warn("Unable to find member {}", memberOrAliasName);
                }

                // Last, because it is the only step that can cost a download per dimension, and because
                // by here the name is neither a member nor a Default alias. The search above compares
                // PbcsMember#getAlias(), which is the Default table by definition, so an alias from any
                // other configured table gets this far and no further without it.
                PbcsMember byAlias = searchConfiguredAliasTables(memberOrAliasName);
                if (byAlias != null) {
                    return byAlias;
                }
            }
            memberResolver.addInvalidMember(this, memberOrAliasName);
        } catch (PbcsKnownInvalidMemberException e) {
            logger.debug("Encountered known invalid member: {}", e.getObjectName());
        }
        return null;
    }

    /**
     * Runs the dimension searches, stopping at the first that finds the member.
     *
     * <p>Sequentially when only one search thread is configured, which is the default, because
     * {@code invokeAny} on a one-thread pool buys no parallelism and costs real work: it submits every
     * search, and when one succeeds it cancels the others - interrupting whichever was already running.
     * That search has usually just downloaded a dimension, and the interruption throws the download
     * away before the dimension can keep it, so the same dimension is fetched again on the next lookup,
     * and the next. Measured against Vision: resolving successive members of Period re-fetched Product
     * every time, for ever.
     *
     * <p>A caller who asked for several threads asked for the speculation, and gets it.
     */
    private PbcsMember search(List<MemberSearchCallable> searchers)
            throws InterruptedException, ExecutionException {
        if (searchThreads > 1) {
            return executorService.invokeAny(searchers);
        }
        Exception lastFailure = null;
        for (MemberSearchCallable searcher : searchers) {
            try {
                PbcsMember found = searcher.call();
                if (found != null) {
                    return found;
                }
            } catch (Exception notHere) {
                lastFailure = notHere;
            }
        }
        throw new ExecutionException("No dimension had that member", lastFailure);
    }

    /**
     * Looks for a name among the aliases of the configured non-Default alias tables.
     *
     * <p>Cloud EPM does not enumerate alias tables, so the ones to look in are the ones the caller
     * named - see {@link PbcsApplication.PlanTypeConfiguration#getAliasTables()}. A plan told about no
     * tables looks in none and this costs nothing, which is what makes it safe to put on the path of
     * every failed lookup.
     *
     * <p>Whatever it finds is written into both caches under the alias that found it, so the next ask
     * is a map lookup: the dimension cache learns where the alias lives, and the member resolver learns
     * the member itself. That is the same pair {@code getMemberOrAlias} writes for a name it resolved
     * the ordinary way.
     *
     * @param aliasName the name to look for
     * @return the member it is an alias of, or null if no configured table has it
     */
    private PbcsMember searchConfiguredAliasTables(String aliasName) {
        for (String aliasTable : getAliasTables()) {
            if (PbcsMember.isDefaultAliasTable(aliasTable)) {
                continue;
            }
            for (PbcsDimension dimension : explicitDimensions) {
                String memberName = getMembersByAlias(dimension.getName(), aliasTable).get(aliasName);
                if (memberName == null) {
                    continue;
                }
                PbcsMember member = getMember(dimension.getName(), memberName);
                if (member == null) {
                    logger.warn("Alias {} in table {} names member {} in {}, which does not resolve",
                            aliasName, aliasTable, memberName, dimension.getName());
                    continue;
                }
                logger.debug("Found member {} (via alias {} in table {}) in dimension {}",
                        memberName, aliasName, aliasTable, dimension.getName());
                memberDimensionCache.setDimension(this, aliasName, dimension.getName());
                memberResolver.setMember(this, aliasName, member);
                return member;
            }
        }
        return null;
    }

    /**
     * Check if the dimension for the member is already in the cache, and check it.
     *
     * @param memberOrAliasName the member or alias name to look for
     * @return the member, if found, null otherwise
     */
    private PbcsMember oneOffSearchInDimension(String memberOrAliasName) {
        String possibleDimension = memberDimensionCache.getDimensionName(this, memberOrAliasName);
        if (possibleDimension != null) {
            PbcsDimension dimension = getDimension(possibleDimension);
            PbcsMember matchingMember = dimension.getRoot().searchForDescendant(memberOrAliasName);
            if (matchingMember == null) {
                logger.warn("Looking in cached dimension {} for {} but couldn't find it, cache is invalid", possibleDimension, memberOrAliasName);
            }
            return matchingMember;
        } else {
            return null;
        }
    }

    /**
     * Gets the names of this plan type's explicit dimensions.
     *
     * @return the dimension names
     */
    protected List<String> getDimensionNames() {
        return explicitDimensions.stream()
                .map(PbcsDimension::getName)
                .toList();
    }

    /**
     * Whether this plan type has an explicit dimension with the given name.
     *
     * @param dimensionName the dimension name
     * @return true if this plan type has that dimension, false otherwise
     */
    public boolean hasDimension(String dimensionName) {
        for (PbcsDimension dimension : explicitDimensions) {
            if (dimension.getName().equals(dimensionName)) return true;
        }
        return false;
    }

    @Override
    public String getCell() {
        return getCell(getDimensionNames());
    }

    @Override
    public DataSliceGrid retrieve() {
        return retrieve(getDimensionNames());
    }

    @Override
    public PbcsMember getMember(String memberName) {
        // TODO: shortcut when member is a dimension name
        String dimensionName = findMemberDimensionFromCache(memberName);
        if (dimensionName == null) {
            logger.debug("Member dimension cache does not contain entry for {}, will search explicit dimensions {}", memberName, explicitDimensions);
            dimensionName = findMemberDimensionFromExplicit(memberName);
            if (dimensionName == null) {
                // It may be an alias rather than a member name. A grid that was retrieved with aliases
                // on hands its headers back as aliases, so exporting one asked this for a name no
                // dimension has - and got an exception naming a member that plainly exists. Tried only
                // after the plain search fails, so a name that is a name costs nothing extra.
                String canonical = canonicalMemberName(memberName, true);
                if (!memberName.equals(canonical)) {
                    logger.debug("{} is an alias of {}; looking that up instead", memberName, canonical);
                    return getMember(canonical);
                }
                throw new PbcsClientException("Unable to determine dimension for member " + memberName + " after searching explicit dimensions");
            }
        }
        return getMember(dimensionName, memberName);
    }

    private String findMemberDimensionFromExplicit(String memberName) {
        for (PbcsDimension dimension : explicitDimensions) {
            try {
                PbcsMember member = getMember(dimension.getName(), memberName);
                if (member != null) {
                    String dimensionName = dimension.getName();
                    memberDimensionCache.setDimension(this, memberName, dimensionName);
                    return dimensionName;
                }
            } catch (PbcsClientException e) {
                logger.debug("Did not find member {} in dimension {}", memberName, dimension.getName());
            }
        }
        return null;
    }

    @Override
    public List<PbcsMember> searchMembers(MemberSearchQuery query) {
        Set<String> searchDimensions = new HashSet<>();

        if (query.getDimensionName() != null) {
            if (!hasDimension(query.getDimensionName())) {
                throw new PbcsNoSuchObjectException(query.getDimensionName(), PbcsObjectType.DIMENSION);
            }
            searchDimensions.add(query.getDimensionName());
        } else {
            if (hasDimension(query.getSearchTerm())) {
                // shortcut for when you're searching without a wildcard and the search term happens to be one of the
                // dimensions
                searchDimensions.add(query.getSearchTerm());
            } else {
                searchDimensions.addAll(getDimensionNames());
            }
        }

        AbstractMemberVisitor memberVisitor = switch (query.getType()) {
            case REGEX -> new SearchRegexMemberVisitor(query);
            case SEARCH_WILD -> new SearchWildMemberVisitor(query);
            case SEARCH -> new SearchMemberVisitor(query);
        };

        logger.info("Searching {}.{} in dimension(s) {} using search query {}", getApplication().getName(), getName(), searchDimensions, query);
        for (String searchDimension : searchDimensions) {
            walkDimension(searchDimension, query.getMemberName(), memberVisitor);
        }
        logger.info("Search returned {} members", memberVisitor.getMatchingMembers().size());
        return memberVisitor.getMatchingMembers();
    }

    /**
     * Walks the member tree for a given dimension and starting member (or all dimensions if none specified), and the
     * root member of each dimension if a starting member isn't specified, calling the given member visitor for each
     * member node.
     *
     * @param dimensionName the dimension to walk, or null if to walk all
     * @param startingMember the starting member, or null if to use root of dimension
     * @param memberVisitor the member visitor to call
     */
    public void walkDimension(String dimensionName, String startingMember, PlanTypeWalker.Visitor memberVisitor) {
        PbcsDimension dimension = getDimension(dimensionName);

        Queue<PbcsMember> members = new ArrayDeque<>();
        members.add(startingMember == null ? dimension.getRoot() : dimension.getMember(startingMember));

        while (!members.isEmpty()) {
            PbcsMember current = members.remove();
            PlanTypeWalker.MemberVisitResult result = memberVisitor.visitMember(this, current);
            if (result == PlanTypeWalker.MemberVisitResult.TERMINATE) break;
            members.addAll(current.getChildren());
        }

    }

    @Override
    public DataSliceGrid retrieve(PovGrid<String> grid, RetrieveOptions retrieveOptions) {
        // get the 'fulcrum' point in the grid
        int firstRowWithCell = GridUtils.firstNonNullInColumn(grid, 0);
        int firstColWithCell = GridUtils.firstNonNullInRow(grid, 0);
        int lastNonNullCol = GridUtils.lastNonNullInRow(grid, 0);

        List<DimensionMembers> top = new ArrayList<>();
        List<String> topDims = retrieveOptions.isProvideDimensionHints() ?
                resolveDimensions(GridUtils.col(grid, firstColWithCell,  0, firstRowWithCell)) :
                null;

        for (int col = firstColWithCell; col <= lastNonNullCol; col++) {
            List<String> members = canonicalMemberNames(GridUtils.col(grid, col, 0, firstRowWithCell));
            DimensionMembers dimensionMembers = new DimensionMembers(topDims, members);
            top.add(dimensionMembers);
        }

        List<DimensionMembers> left = new ArrayList<>();
        List<String> leftDims = retrieveOptions.isProvideDimensionHints() ?
                resolveDimensions(GridUtils.row(grid, firstRowWithCell, 0, firstColWithCell)) :
                null;

        for (int row = firstRowWithCell; row < grid.getRows(); row++) {
            List<String> members = canonicalMemberNames(GridUtils.row(grid, row, 0, firstColWithCell));
            DimensionMembers dimensionMembers = new DimensionMembers(leftDims, members);
            left.add(dimensionMembers);
        }

        // The POV as well: an alias is as likely there as on an axis, and the server rejects the whole
        // request over one member it cannot place.
        GridDefinition gridDefinition =
                new GridDefinition(canonicalMemberNames(grid.getPov()), top, left);
        gridDefinition.setSuppressMissingRows(retrieveOptions.isSuppressMissingRows());
        gridDefinition.setSuppressMissingColumns(retrieveOptions.isSuppressMissingColumns());
        ExportDataSlice exportDataSlice = new ExportDataSlice(gridDefinition);
        exportDataSlice.setExportPlanningData(retrieveOptions.isExportPlanningData());

        // todo: catch exception and provide custom with some analysis on potential causes of problem
        try {
            DataSlice slice = DataSlicePageRetriever.retrieve(exportDataSlice, retrieveOptions, pageRequest -> {
                logger.debug("Exporting {} data {}", getQualifiedName(), pageRequest);
                return post("applications/{application}/plantypes/{planType}/exportdataslice", pageRequest,
                        DataSlice.class, getApplication().getName(), getName());
            });
            // firstColWithCell rather than leftDims.size(): both count the row-header columns, but the
            // hints are optional and null when they were not asked for - so reading the size off them
            // made a retrieve without dimension hints fail with a NullPointerException instead of
            // returning a grid.
            return new DataSliceGrid(this, slice, firstColWithCell);
        } catch (Exception e) {
            throw new PbcsDataExportException(grid, e);
        }
    }

    @Override
    public void cache() {
        logger.info("Caching outline for {}, dimensions: {}", getName(), getDimensionNames());
        CachingMemberResolverVisitor visitor = new CachingMemberResolverVisitor();

        PlanTypeWalker.Options options = new PlanTypeWalker.Options();
        PlanTypeWalker.walk(this, visitor, options);

        logger.info("Finished walking outline for {}", getName());
    }

    private List<String> resolveDimensions(List<String> members) {
        List<String> dimensions = new ArrayList<>();
        for (String memberName : members) {
            PbcsMember member = getMemberOrAlias(memberName);
            if (member == null) {
                throw new PbcsClientException("Unable to resolve member/dimension for " + memberName);
            } else {
                dimensions.add(member.getDimensionName());
            }
        }
        return dimensions;
    }

    @Override
    public PbcsPov createPov() {
        List<PbcsMember> members = new ArrayList<>();
        for (PbcsDimension dim : getRealDimensions()) {
            members.add(dim.getRoot());
        }
        return new PbcsPovImpl(this, members);
    }

    @Override
    public PbcsPov createPov(String... members) {
        PbcsPov pov = createPov();
        for (String member : members) {
            pov = pov.member(member);
        }
        return pov;
    }

    private List<PbcsDimension> getRealDimensions() {
        List<PbcsDimension> dimensions = new ArrayList<>();
        for (PbcsDimension dim : getDimensions()) {
            if (dim.getDimensionType() != PbcsMemberType.ATTRIBUTE) {
                dimensions.add(dim);
            }
        }
        return dimensions;
    }

    private static class MemberSearchCallable implements Callable<PbcsMember> {

        private final PbcsDimension dimension;

        private final String memberOrAliasName;

        private MemberSearchCallable(PbcsDimension searchDimension, String memberOrAliasName) {
            this.dimension = searchDimension;
            this.memberOrAliasName = memberOrAliasName;
        }

        @Override
        public PbcsMember call() {
            logger.debug("Searching dimension {} for member/alias {}", dimension.getName(), memberOrAliasName);
            PbcsMember rootMember = dimension.getRoot();

            PbcsMember matchingMember = rootMember.searchForDescendant(memberOrAliasName);
            if (matchingMember != null) {
                return matchingMember;
            } else {
                throw new PbcsInvalidMemberException("Unable to find member or alias " + memberOrAliasName + " in dimension " + dimension.getName());
            }
        }

    }

    private class CachingMemberResolverVisitor extends PlanTypeWalker.AbstractVisitor implements PlanTypeWalker.Visitor {

        private int numCached;

        @Override
        public void endPlan(PbcsPlanType plan) {
            logger.info("Finished walking outline of {}, cached {} items", plan.getName(), numCached);
        }

        @Override
        public PlanTypeWalker.MemberVisitResult startDimension(PbcsDimension dimension) {
            logger.info("Starting dimension {}", dimension);
            return PlanTypeWalker.MemberVisitResult.CONTINUE;
        }

        @Override
        public void endDimension(PbcsDimension dimension) {
            logger.info("Finished dimension {}", dimension);
        }

        @Override
        public PlanTypeWalker.MemberVisitResult visitMember(PbcsPlanType planType, PbcsMember member) {
            numCached += cacheMember(memberResolver, planType, member, getConfiguration().isIgnoreAliases());
            return PlanTypeWalker.MemberVisitResult.CONTINUE;
        }

    }

    static int cacheMember(PbcsPlanType.MemberResolver memberResolver, PbcsPlanType planType, PbcsMember member, boolean ignoreAliases) {
        memberResolver.setMember(planType, member.getName(), member);
        int cached = 1;
        if (ignoreAliases) {
            return cached;
        }
        for (String alias : aliasesOf(planType, member)) {
            memberResolver.setMember(planType, alias, member);
            cached++;
        }
        return cached;
    }

    /**
     * Every alias a member has, across the alias tables the plan was told about.
     *
     * <p>This used to be {@code member.getAlias()} alone, which is the Default table - so a bulk
     * {@link #cache()} left every other table's aliases unresolvable, and the cache a caller had just
     * paid to build did not contain the names they were about to look up.
     *
     * <p>Costs one dimension read per alias table, not one per member: the alias tree is fetched whole
     * and cached, so walking an outline asks the server once per dimension and table however many
     * members it visits. An alias identical to the member's name is left out, as is a repeat - there is
     * no sense in mapping one name to two members, and first past the post is what the reverse index
     * does too.
     */
    private static List<String> aliasesOf(PbcsPlanType planType, PbcsMember member) {
        List<String> aliases = new ArrayList<>();
        for (String aliasTable : planType.getAliasTables()) {
            String alias = PbcsMember.isDefaultAliasTable(aliasTable)
                    ? member.getAlias()
                    : planType.getMemberAlias(member.getDimensionName(), member.getName(), aliasTable);
            if (alias != null && !alias.isEmpty()
                    && !alias.equals(member.getName()) && !aliases.contains(alias)) {
                aliases.add(alias);
            }
        }
        return aliases;
    }

}
