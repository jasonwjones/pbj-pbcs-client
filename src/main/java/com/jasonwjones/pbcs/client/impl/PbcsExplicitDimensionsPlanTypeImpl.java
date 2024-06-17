package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.api.v3.dataslices.DataSlice;
import com.jasonwjones.pbcs.api.v3.dataslices.DimensionMembers;
import com.jasonwjones.pbcs.api.v3.dataslices.ExportDataSlice;
import com.jasonwjones.pbcs.api.v3.dataslices.GridDefinition;
import com.jasonwjones.pbcs.client.*;
import com.jasonwjones.pbcs.client.exceptions.PbcsClientException;
import com.jasonwjones.pbcs.client.exceptions.PbcsInvalidDimensionException;
import com.jasonwjones.pbcs.client.exceptions.PbcsNoSuchObjectException;
import com.jasonwjones.pbcs.client.impl.grid.DataSliceGrid;
import com.jasonwjones.pbcs.client.impl.membervisitors.AbstractMemberVisitor;
import com.jasonwjones.pbcs.client.impl.membervisitors.SearchMemberVisitor;
import com.jasonwjones.pbcs.client.impl.membervisitors.SearchRegexMemberVisitor;
import com.jasonwjones.pbcs.client.impl.membervisitors.SearchWildMemberVisitor;
import com.jasonwjones.pbcs.util.GridPrinter;
import com.jasonwjones.pbcs.util.GridUtils;
import com.jasonwjones.pbcs.util.PlanTypeWalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * A plan type implementation where the known dimensions are explicitly defined. Defining the list of explicit dimensions
 * is usually handled by having an additional property on the connection settings. For example, an additional query
 * parameter can be appended to your cube/plan name, such as <code>Basic?dimensions=Period;Years;Scenario</code>.
 *
 * <p>Hopefully someday the process of getting the dimensions for a plan will be better supported by the EPM Cloud REST
 * API but for now this lets us significantly enrich functionality.
 */
public class PbcsExplicitDimensionsPlanTypeImpl extends PbcsPlanTypeImpl implements PbcsExplicitDimensionsPlanType {

    private static final Logger logger = LoggerFactory.getLogger(PbcsExplicitDimensionsPlanTypeImpl.class);

    private final List<PbcsDimension> explicitDimensions;

    private final ExecutorService executorService;

    PbcsExplicitDimensionsPlanTypeImpl(RestContext context, PbcsApplication application, PbcsApplication.PlanTypeConfiguration configuration) {
        super(context, application, configuration);

        List<String> dimensionNames = new ArrayList<>();

        if (configuration.isQueryDimensions()) {
            if (configuration.isQueryDimensions()) {
                List<PbcsDimension> dimensions = application.getDimensions(configuration.getName());
                for (PbcsDimension dimension : dimensions) {
                    dimensionNames.add(dimension.getName());
                }
            }
        } else {
            if (configuration.getExplicitDimensions() == null || configuration.getExplicitDimensions().isEmpty()) throw new IllegalArgumentException("Explicit dimension list cannot be empty");
            dimensionNames.addAll(configuration.getExplicitDimensions());
        }

        if (dimensionNames.isEmpty()) {
            throw new IllegalArgumentException("Dimension name list cannot be empty: provide dimension names or enable query dimensions");
        }

        this.explicitDimensions = new ArrayList<>();

        int dimNumber = 0;

        for (String dimName : dimensionNames) {
            PbcsMemberType type = configuration.isValidateDimensions() ?
                    application.getMember(dimName, dimName).getType() :
                    PbcsMemberType.UNKNOWN;
            this.explicitDimensions.add(new ExplicitDimension(dimName, dimNumber++, type));
        }

        // add in explicit attribute dimensions, if any
        if (configuration.getExplicitAttributeDimensions() != null) {
            for (String attribDimName : configuration.getExplicitAttributeDimensions()) {
                PbcsMemberType type = configuration.isValidateDimensions() ?
                        application.getMember(attribDimName, attribDimName).getType() :
                        PbcsMemberType.ATTRIBUTE;
                this.explicitDimensions.add(new ExplicitDimension(attribDimName, dimNumber++, type));
            }
        }

        logger.debug("Using {} thread(s) to perform member name/alias search", configuration.getMemberSearchThreads());
        executorService = Executors.newFixedThreadPool(configuration.getMemberSearchThreads());
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
        PbcsMember member = memberResolver.getMember(this, memberOrAliasName);
        if (member != null) {
            return member;
        } else {
            logger.warn("Resolving {} from source", memberOrAliasName);
            PbcsMember matchingMember = oneOffSearchInDimension(memberOrAliasName);
            if (matchingMember != null) return matchingMember;

            List<MemberSearchCallable> searchers;
            if (getDimensionNames().contains(memberOrAliasName)) {
                // shortcut when the member being queried literally is a dimension
                PbcsDimension searchDimension = getDimension(memberOrAliasName);
                searchers = Collections.singletonList(new MemberSearchCallable(searchDimension, memberOrAliasName));
            } else {
                // you can technically re-search a dimension, but that only happens when you have a bad cache
                searchers = explicitDimensions.stream()
                        .map(dimension -> new MemberSearchCallable(dimension, memberOrAliasName))
                        .collect(Collectors.toList());
            }

            try {
                member = executorService.invokeAny(searchers);
                logger.debug("Found member {} (via {}) in dimension {}", member.getName(), memberOrAliasName, member.getDimensionName());
                memberDimensionCache.setDimension(this, memberOrAliasName, member.getDimensionName());
                memberResolver.setMember(this, memberOrAliasName, member);
                return member;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                logger.warn("Unable to find member {}", memberOrAliasName);
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

    protected List<String> getDimensionNames() {
        return explicitDimensions.stream()
                .map(PbcsDimension::getName)
                .collect(Collectors.toList());
    }

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
            logger.debug("Member dimension cache does not contain entry for {}, will search explicitly dimensions {}", memberName, explicitDimensions);
            dimensionName = findMemberDimensionFromExplicit(memberName);
            if (dimensionName == null) {
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
            searchDimensions.addAll(getDimensionNames());
        }

        AbstractMemberVisitor memberVisitor;
        switch (query.getType()) {
            case REGEX:
                memberVisitor = new SearchRegexMemberVisitor(query.getSearchTerm());
                break;
            case SEARCH_WILD:
                memberVisitor = new SearchWildMemberVisitor(query.getSearchTerm());
                break;
            case SEARCH:
                memberVisitor = new SearchMemberVisitor(query.getSearchTerm());
                break;
            default:
                throw new IllegalArgumentException("Unknown search type: " + query.getType());
        }

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
            List<String> members = GridUtils.col(grid, col, 0, firstRowWithCell);
            DimensionMembers dimensionMembers = new DimensionMembers(topDims, members);
            top.add(dimensionMembers);
        }

        List<DimensionMembers> left = new ArrayList<>();
        List<String> leftDims = retrieveOptions.isProvideDimensionHints() ?
                resolveDimensions(GridUtils.row(grid, firstRowWithCell, 0, firstColWithCell)) :
                null;

        for (int row = firstRowWithCell; row < grid.getRows(); row++) {
            List<String> members = GridUtils.row(grid, row, 0, firstColWithCell);
            DimensionMembers dimensionMembers = new DimensionMembers(leftDims, members);
            left.add(dimensionMembers);
        }

        GridDefinition gridDefinition = new GridDefinition(grid.getPov(), top, left);
        ExportDataSlice exportDataSlice = new ExportDataSlice(gridDefinition);
        exportDataSlice.setExportPlanningData(retrieveOptions.isExportPlanningData());

        // todo: catch exception and provide custom with some analysis on potential causes of problem
        try {
            logger.debug("Exporting data slice from {}", getName());
            DataSlice slice = post("applications/{application}/plantypes/{planType}/exportdataslice", exportDataSlice, DataSlice.class, getApplication().getName(), getName());
            logger.debug("Data slice export returned from {}", getName());
            return new DataSliceGrid(this, slice);
        } catch (Exception e) {
            logger.error("Unable to retrieve grid: {}", e.getMessage());
            if (logger.isDebugEnabled()) {
                logger.debug("Retrieval grid:");
                GridPrinter.print(grid);
            }
            throw e;
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

    private class ExplicitDimension implements PbcsDimension {

        private final String name;

        private final int number;

        private final PbcsMemberType type;

        private ExplicitDimension(String name, int number, PbcsMemberType type) {
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
            return PbcsExplicitDimensionsPlanTypeImpl.this.getMember(name, memberName);
        }

        @Override
        public PbcsMemberType getDimensionType() {
            return type;
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
                throw new RuntimeException("Couldn't find " + memberOrAliasName + " in dimension " + dimension.getName());
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
            memberResolver.setMember(planType, member.getName(), member);
            numCached++;
            if (member.getAlias() != null && !member.getAlias().isEmpty() && !member.getName().equals(member.getAlias())) {
                memberResolver.setMember(planType, member.getAlias(), member);
                numCached++;
            }
            return PlanTypeWalker.MemberVisitResult.CONTINUE;
        }

    }

}