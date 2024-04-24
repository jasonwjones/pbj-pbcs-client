package com.jasonwjones.pbcs.client.impl.membervisitors;

import com.jasonwjones.pbcs.client.PbcsMember;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.util.PlanTypeWalker;

public class SearchMemberVisitor extends AbstractMemberVisitor {

    private final String memberName;

    public SearchMemberVisitor(String memberName) {
        this.memberName = memberName;
    }

    @Override
    public PlanTypeWalker.MemberVisitResult visitMember(PbcsPlanType planType, PbcsMember member) {
        if (member.getName().equalsIgnoreCase(memberName)) {
            addMember(member);
            return PlanTypeWalker.MemberVisitResult.TERMINATE;
        }
        return PlanTypeWalker.MemberVisitResult.CONTINUE;
    }

}