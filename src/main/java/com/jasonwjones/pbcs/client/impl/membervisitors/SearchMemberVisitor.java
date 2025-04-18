package com.jasonwjones.pbcs.client.impl.membervisitors;

import com.jasonwjones.pbcs.client.MemberSearchQuery;
import com.jasonwjones.pbcs.client.PbcsMember;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.util.PlanTypeWalker;

public class SearchMemberVisitor extends AbstractMemberVisitor {

    private final String memberName;

    private final boolean caseSensitive;

    public SearchMemberVisitor(MemberSearchQuery query) {
        super(query.isSearchAliases());
        this.memberName = query.getSearchTerm();
        this.caseSensitive = query.isCaseSensitive();
    }

    @Override
    public PlanTypeWalker.MemberVisitResult visitMember(PbcsPlanType planType, PbcsMember member) {
        if (matches(member.getName()) || (isIncludeAliases() && matches(member.getAlias()))) {
            addMember(member);
            return PlanTypeWalker.MemberVisitResult.TERMINATE;
        }
        return PlanTypeWalker.MemberVisitResult.CONTINUE;
    }

    private boolean matches(String nameOrAlias) {
        if (nameOrAlias == null) return false;
        return (caseSensitive && nameOrAlias.equals(memberName)) || (!caseSensitive && nameOrAlias.equalsIgnoreCase(memberName));
    }

}