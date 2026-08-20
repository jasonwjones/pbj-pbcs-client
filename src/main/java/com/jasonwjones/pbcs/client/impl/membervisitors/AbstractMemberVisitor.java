package com.jasonwjones.pbcs.client.impl.membervisitors;

import com.jasonwjones.pbcs.client.MemberSearchQuery;
import com.jasonwjones.pbcs.client.PbcsMember;
import com.jasonwjones.pbcs.util.PlanTypeWalker;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for {@link PlanTypeWalker.Visitor} implementations that collect members matching the criteria
 * of a {@link MemberSearchQuery} while walking a plan type's dimension hierarchy.
 */
public abstract class AbstractMemberVisitor extends PlanTypeWalker.AbstractVisitor implements PlanTypeWalker.Visitor {

    private final MemberSearchQuery memberSearchQuery;

    private final List<PbcsMember> matchingMembers = new ArrayList<>();

    /**
     * Constructs an instance for the given search query.
     *
     * @param memberSearchQuery the search query whose criteria and options this visitor should honor
     */
    protected AbstractMemberVisitor(MemberSearchQuery memberSearchQuery) {
        this.memberSearchQuery = memberSearchQuery;
    }

    /**
     * Gets the members collected so far by this visitor.
     *
     * @return the matching members
     */
    public List<PbcsMember> getMatchingMembers() {
        return matchingMembers;
    }

    /**
     * Adds a member to the matching members list, honoring the search query's shared-member exclusion option.
     *
     * @param member the member to add
     */
    protected void addMember(PbcsMember member) {
        boolean excludeShare = memberSearchQuery.isExcludeShares() && member.getDataStorageType() == PbcsMember.DataStorage.SHARED;
        if (!excludeShare) matchingMembers.add(member);
    }

    /**
     * Whether the search query requests that aliases also be considered when matching members.
     *
     * @return true if aliases should be included, false otherwise
     */
    protected boolean isIncludeAliases() {
        return memberSearchQuery.isSearchAliases();
    }

    /**
     * Whether the search query requests that the walk stop as soon as a match is found.
     *
     * @return true if the walk should stop when found, false otherwise
     */
    protected boolean isStopWhenFound() {
        return memberSearchQuery.isStopWhenFound();
    }

}