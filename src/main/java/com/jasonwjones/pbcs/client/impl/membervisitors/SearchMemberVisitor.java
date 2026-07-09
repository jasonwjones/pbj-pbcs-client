package com.jasonwjones.pbcs.client.impl.membervisitors;

import com.jasonwjones.pbcs.client.MemberSearchQuery;
import com.jasonwjones.pbcs.client.PbcsMember;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.util.PlanTypeWalker;

public class SearchMemberVisitor extends AbstractMemberVisitor {

    private final String memberName;

    private final boolean caseSensitive;

    public SearchMemberVisitor(MemberSearchQuery memberSearchQuery) {
        super(memberSearchQuery);
        this.memberName = memberSearchQuery.getSearchTerm();
        this.caseSensitive = memberSearchQuery.isCaseSensitive();
    }

    @Override
    public PlanTypeWalker.MemberVisitResult visitMember(PbcsPlanType planType, PbcsMember member) {
        if (matches(member.getName()) || (isIncludeAliases() && matches(member.getAlias()))) {
            addMember(member);
            if (isStopWhenFound()) return PlanTypeWalker.MemberVisitResult.TERMINATE;
        }
        return PlanTypeWalker.MemberVisitResult.CONTINUE;
    }

    private boolean matches(String nameOrAlias) {
        if (nameOrAlias == null) return false;
        return (caseSensitive && nameOrAlias.equals(memberName)) || (!caseSensitive && nameOrAlias.equalsIgnoreCase(memberName));
    }

}