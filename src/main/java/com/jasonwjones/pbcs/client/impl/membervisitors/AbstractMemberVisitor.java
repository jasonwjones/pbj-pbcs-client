package com.jasonwjones.pbcs.client.impl.membervisitors;

import com.jasonwjones.pbcs.client.MemberSearchQuery;
import com.jasonwjones.pbcs.client.PbcsMember;
import com.jasonwjones.pbcs.util.PlanTypeWalker;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMemberVisitor extends PlanTypeWalker.AbstractVisitor implements PlanTypeWalker.Visitor {

    private final MemberSearchQuery memberSearchQuery;

    private final List<PbcsMember> matchingMembers = new ArrayList<>();

    protected AbstractMemberVisitor(MemberSearchQuery memberSearchQuery) {
        this.memberSearchQuery = memberSearchQuery;
    }

    public List<PbcsMember> getMatchingMembers() {
        return matchingMembers;
    }

    protected void addMember(PbcsMember member) {
        boolean excludeShare = memberSearchQuery.isExcludeShares() && member.getDataStorageType() == PbcsMember.DataStorage.SHARED;
        if (!excludeShare) matchingMembers.add(member);
    }

    protected boolean isIncludeAliases() {
        return memberSearchQuery.isSearchAliases();
    }

    protected boolean isStopWhenFound() {
        return memberSearchQuery.isStopWhenFound();
    }

}