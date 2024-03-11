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
import com.jasonwjones.pbcs.util.GridUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.nio.file.FileVisitResult;
import java.util.*;
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

    PbcsExplicitDimensionsPlanTypeImpl(RestContext context, PbcsApplication application, PbcsApplication.PlanTypeConfiguration configuration) {
        super(context, application, configuration.getName(), configuration.getMemberDimensionCache());
        Objects.requireNonNull(configuration.getExplicitDimensions());
        if (configuration.getExplicitDimensions().isEmpty()) throw new IllegalArgumentException("Explicit dimension list cannot be empty");
        this.explicitDimensions = new ArrayList<>();

        int dimNumber = 0;

        for (String dimName : configuration.getExplicitDimensions()) {
            PbcsMemberType type = configuration.isValidateDimensions() ?
                    application.getMember(dimName, dimName).getType() :
                    PbcsMemberType.UNKNOWN;
            this.explicitDimensions.add(new ExplicitDimension(dimName, dimNumber++, type));
        }

        // add in explicit attribute dimensions, if any
        for (String attribDimName : configuration.getExplicitAttributeDimensions()) {
            PbcsMemberType type = configuration.isValidateDimensions() ?
                    application.getMember(attribDimName, attribDimName).getType() :
                    PbcsMemberType.ATTRIBUTE;
            this.explicitDimensions.add(new ExplicitDimension(attribDimName, dimNumber++, type));
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
    public PbcsMemberProperties getMemberOrAlias(String memberOrAliasName) {
        // check for known dimension name for the member or alias in the member to dimension lookup cache
        String possibleDimension = memberDimensionCache.getDimensionName(this, memberOrAliasName);

        // we *assume* that the dimension in the lookup cache is valid, but leave ourselves some wiggle room just in the
        // extremely unlikely case that it's somehow wrong (or outdated), and we need to perform the rest of the brute-force
        // search anyway
        List<PbcsDimension> dimensionsToSearch = explicitDimensions;
        if (possibleDimension != null) {
            dimensionsToSearch = new ArrayList<>(explicitDimensions);
            for (int index = 0; index < dimensionsToSearch.size(); index++) {
                PbcsDimension current = dimensionsToSearch.get(index);
                if (current.getName().equals(possibleDimension)) {
                    // move the presumed dimension to the top of the search order
                    dimensionsToSearch.remove(index);
                    dimensionsToSearch.add(0, current);
                    break;
                }
            }
        }

        for (PbcsDimension dimension : dimensionsToSearch) {
            logger.debug("Searching dimension {} for member/alias {}", dimension.getName(), memberOrAliasName);

            Queue<PbcsMemberProperties> members = new ArrayDeque<>();
            members.add(dimension.getRoot());

            while (!members.isEmpty()) {
                PbcsMemberProperties current = members.remove();
                if (memberOrAliasName.equalsIgnoreCase(current.getName()) || memberOrAliasName.equalsIgnoreCase(current.getAlias())) {
                    // this is technically unneeded if the dimension is the same as possibleDimension, but it will be
                    // set here anyway in case the underlying cache mechanism needs a "hit" in order to update a TTL
                    // or similar value. Note: this could cause a lot of traffic to your SoR if the cache writes through
                    // You may want to use a putIfAbsent paradigm (instead of a put) to avoid unnecessary writes
                    memberDimensionCache.setDimension(this, memberOrAliasName, dimension.getName());
                    return current;
                }
                members.addAll(current.getChildren());
            }
        }
        return null;
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
    public PbcsMemberProperties getMember(String memberName) {
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
                PbcsMemberProperties memberProperties = getMember(dimension.getName(), memberName);
                if (memberProperties != null) {
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
    public List<PbcsMemberProperties> searchMembers(MemberSearchQuery query) {
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
    public void walkDimension(String dimensionName, String startingMember, MemberVisitor memberVisitor) {
        PbcsDimension dimension = getDimension(dimensionName);

        Queue<PbcsMemberProperties> members = new ArrayDeque<>();
        members.add(startingMember == null ? dimension.getRoot() : dimension.getMember(startingMember));

        while (!members.isEmpty()) {
            PbcsMemberProperties current = members.remove();
            FileVisitResult result = memberVisitor.preVisitMember(current);
            if (result == FileVisitResult.TERMINATE) break;
            members.addAll(current.getChildren());
        }

    }

    @Override
    public DataSliceGrid retrieve(PovGrid<String> grid, RetrieveOptions options) {
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

        // TODO: check value of isProvideDimensionHints
        List<String> leftDims = resolveDimensions(GridUtils.row(grid, firstRowWithCell, 0, firstColWithCell));

        for (int row = firstRowWithCell; row < grid.getRows(); row++) {
            List<String> members = GridUtils.row(grid, row, 0, firstColWithCell);
            DimensionMembers dimensionMembers = new DimensionMembers(leftDims, members);
            left.add(dimensionMembers);
        }

        GridDefinition gridDefinition = new GridDefinition(grid.getPov(), top, left);
        ExportDataSlice exportDataSlice = new ExportDataSlice(gridDefinition);

        try {
            ResponseEntity<DataSlice> slice = this.context.getTemplate().postForEntity(this.context.getBaseUrl() + "applications/{application}/plantypes/{planType}/exportdataslice", exportDataSlice, DataSlice.class, getApplication().getName(), getName());
            if (slice.getStatusCode().is2xxSuccessful()) {
                DataSlice dataSlice = slice.getBody();
                return new DataSliceGrid(this, dataSlice);
            } else {
                throw new RuntimeException("Error retrieving data, received code: " + slice.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("Exception: {}", e.getMessage());
            throw e;
        }
    }

    private List<String> resolveDimensions(List<String> members) {
        List<String> dimensions = new ArrayList<>();
        for (String member : members) {
            PbcsMemberProperties properties = getMemberOrAlias(member);
            if (properties == null) {
                throw new PbcsClientException("Unable to resolve member/dimension for " + member);
            } else {
                dimensions.add(properties.getDimensionName());
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
        public PbcsMemberProperties getMember(String memberName) {
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

}