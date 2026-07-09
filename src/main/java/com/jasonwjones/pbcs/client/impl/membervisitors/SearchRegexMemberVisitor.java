package com.jasonwjones.pbcs.client.impl.membervisitors;

import com.jasonwjones.pbcs.client.MemberSearchQuery;
import com.jasonwjones.pbcs.client.PbcsMember;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.util.PlanTypeWalker;

import java.util.regex.Pattern;

public class SearchRegexMemberVisitor extends AbstractMemberVisitor {

    protected final Pattern pattern;

    public SearchRegexMemberVisitor(MemberSearchQuery query) {
        this(query, Pattern.compile(query.getSearchTerm(), query.isCaseSensitive() ? Pattern.CASE_INSENSITIVE : 0));
    }

    protected SearchRegexMemberVisitor(MemberSearchQuery query, Pattern pattern) {
        super(query);
        this.pattern = pattern;
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
        return pattern.matcher(nameOrAlias).matches();
    }

}