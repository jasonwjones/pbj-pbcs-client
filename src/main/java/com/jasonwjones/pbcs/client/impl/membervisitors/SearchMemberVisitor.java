package com.jasonwjones.pbcs.client.impl.membervisitors;

import com.jasonwjones.pbcs.client.PbcsMember;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.util.PlanTypeWalker;

public class SearchMemberVisitor extends AbstractMemberVisitor {

    private final String memberName;

    private final boolean caseSensitive;

    public SearchMemberVisitor(String memberName) {
        this(memberName, false);
    }

    public SearchMemberVisitor(String memberName, boolean caseSensitive) {
        this.memberName = memberName;
        this.caseSensitive = caseSensitive;
    }

    @Override
    public PlanTypeWalker.MemberVisitResult visitMember(PbcsPlanType planType, PbcsMember member) {
        if ((caseSensitive && member.getName().equals(memberName)) || (!caseSensitive && member.getName().equalsIgnoreCase(memberName))) {
            addMember(member);
            return PlanTypeWalker.MemberVisitResult.TERMINATE;
        }
        return PlanTypeWalker.MemberVisitResult.CONTINUE;
    }

}