package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.client.MemberSearchQuery;
import com.jasonwjones.pbcs.client.PbcsMember;
import com.jasonwjones.pbcs.client.PbcsMemberQueryType;
import com.jasonwjones.pbcs.client.PbcsObjectType;
import com.jasonwjones.pbcs.client.exceptions.PbcsNoSuchObjectException;
import com.jasonwjones.pbcs.testing.ReadOnlyIntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThrows;

@Category(ReadOnlyIntegrationTest.class)
public class PbcsPlanTypeImplMemberQueryIT extends AbstractVisionCubeIT {

    private static final String INVALID_MEMBER = "XXX_Q1";

    @Test
    public void getDimensions() {
        assertThat(cube.getDimensions().size(), is(DIMENSIONS.size()));
    }

    @Test
    public void searchUsingInvalidMember() {
        PbcsNoSuchObjectException exception = assertThrows(PbcsNoSuchObjectException.class, () -> cube.queryMembers(INVALID_MEMBER, PbcsMemberQueryType.CHILDREN));
        assertThat(exception.getObjectName(), is(INVALID_MEMBER));
        assertThat(exception.getObjectType(), is(PbcsObjectType.MEMBER));
    }

    @Test
    public void searchForChildren() {
        List<PbcsMember> children = cube.queryMembers("Q1", PbcsMemberQueryType.CHILDREN);
        assertThat(children, hasSize(3));
    }

    @Test
    public void quarterSiblingsContainsAllIncludingMember() {
        assertThat(names(cube.queryMembers("Q1", PbcsMemberQueryType.ISIBLINGS)), contains("Q1", "Q2", "Q3", "Q4"));
    }

    @Test
    public void quarterSiblingsContainsAllExcludingMember() {
        assertThat(names(cube.queryMembers("Q1", PbcsMemberQueryType.SIBLINGS)), contains("Q2", "Q3", "Q4"));
    }

    @Test
    public void searchForNonExistingMember() {
        MemberSearchQueryImpl query = new MemberSearchQueryImpl();
        query.setDimensionName("Period");
        query.setSearchTerm("Foo");
        query.setType(MemberSearchQuery.Type.SEARCH);
        List<PbcsMember> members = cube.searchMembers(query);
        assertThat(members, empty());
    }

    @Test
    public void searchForSingleMember() {
        MemberSearchQueryImpl query = new MemberSearchQueryImpl();
        query.setDimensionName("Period");
        query.setSearchTerm("Jan");
        query.setType(MemberSearchQuery.Type.SEARCH);
        List<PbcsMember> members = cube.searchMembers(query);
        assertThat(names(members), contains("Jan"));
    }

    @Test
    public void searchWild() {
        MemberSearchQueryImpl query = new MemberSearchQueryImpl();
        query.setDimensionName("Period");
        query.setSearchTerm("Q*");
        query.setType(MemberSearchQuery.Type.SEARCH_WILD);
        List<PbcsMember> members = cube.searchMembers(query);
        assertThat(names(members), contains("Q-T-D", "Q1", "Q2", "Q3", "Q4"));
    }

    @Test
    public void searchWildWithStartingMember() {
        MemberSearchQueryImpl query = new MemberSearchQueryImpl();
        query.setDimensionName("Period");
        query.setMemberName("YearTotal");
        query.setSearchTerm("Q*");
        query.setType(MemberSearchQuery.Type.SEARCH_WILD);
        List<PbcsMember> members = cube.searchMembers(query);
        assertThat(names(members), contains("Q1", "Q2", "Q3", "Q4"));
    }

    @Test
    public void searchWildJ() {
        MemberSearchQueryImpl query = new MemberSearchQueryImpl();
        query.setDimensionName("Period");
        query.setSearchTerm("J*");
        query.setType(MemberSearchQuery.Type.SEARCH_WILD);
        List<PbcsMember> members = cube.searchMembers(query);
        assertThat(names(members), contains("Jan", "Jun", "Jul"));
    }

    @Test
    public void searchWildMultiple() {
        MemberSearchQueryImpl query = new MemberSearchQueryImpl();
        query.setDimensionName("Product");
        query.setSearchTerm("P_1*0");
        query.setType(MemberSearchQuery.Type.SEARCH_WILD);
        List<PbcsMember> members = cube.searchMembers(query);
        assertThat(names(members), hasSize(7));
    }

    @Test
    public void searchRegexMultiple() {
        MemberSearchQueryImpl query = new MemberSearchQueryImpl();
        query.setDimensionName("Product");
        query.setSearchTerm("P_1[2-4]0");
        query.setType(MemberSearchQuery.Type.SEARCH_WILD);
        List<PbcsMember> members = cube.searchMembers(query);
        assertThat(names(members), hasSize(3));
    }

    @Test
    public void whenGetMemberWithAlias() {
        PbcsMember mgmtRollup = cube.getMemberOrAlias("Management Rollup");
        PbcsMember hardware = cube.getMemberOrAlias("4110: Hardware");

        System.out.println("Dim: " + mgmtRollup + " dim: " + mgmtRollup.getDimensionName());
        System.out.println("Dim: " + hardware + " dim: " + hardware.getDimensionName());
    }

    @Test
    public void whenSearchHasShareButNotExclude() {
        MemberSearchQueryImpl query = new MemberSearchQueryImpl();
        query.setDimensionName("Entity");
        query.setType(MemberSearchQuery.Type.SEARCH_WILD);
        query.setSearchAliases(true);
        query.setSearchTerm("*Facilities*");
        List<PbcsMember> results = cube.searchMembers(query);
        assertThat(results, hasSize(4));
    }

    @Test
    public void whenSearchHasShareAndShouldExclude() {
        MemberSearchQueryImpl query = new MemberSearchQueryImpl();
        query.setDimensionName("Entity");
        query.setType(MemberSearchQuery.Type.SEARCH_WILD);
        query.setSearchAliases(true);
        query.setSearchTerm("*Facilities*");
        query.setExcludeShares(true);
        List<PbcsMember> results = cube.searchMembers(query);
        assertThat(results, hasSize(2));
    }

    private static List<String> names(Collection<PbcsMember> members) {
        return members.stream().map(PbcsMember::getName).toList();
    }

}
