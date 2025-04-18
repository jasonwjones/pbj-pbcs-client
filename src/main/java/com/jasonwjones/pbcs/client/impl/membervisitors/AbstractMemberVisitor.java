package com.jasonwjones.pbcs.client.impl.membervisitors;

import com.jasonwjones.pbcs.client.PbcsMember;
import com.jasonwjones.pbcs.util.PlanTypeWalker;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMemberVisitor extends PlanTypeWalker.AbstractVisitor implements PlanTypeWalker.Visitor {

    private final boolean includeAliases;

    private final List<PbcsMember> matchingMembers = new ArrayList<>();

    protected AbstractMemberVisitor(boolean includeAliases) {
        this.includeAliases = includeAliases;
    }

    public List<PbcsMember> getMatchingMembers() {
        return matchingMembers;
    }

    protected void addMember(PbcsMember member) {
        matchingMembers.add(member);
    }

    protected boolean isIncludeAliases() {
        return includeAliases;
    }

}