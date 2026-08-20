package com.jasonwjones.pbcs.client.impl.membervisitors;

import com.jasonwjones.pbcs.client.MemberSearchQuery;
import com.jasonwjones.pbcs.client.PbcsMember;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.util.PlanTypeWalker;

import java.util.regex.Pattern;

/**
 * A {@link AbstractMemberVisitor} that matches members whose name or alias fully matches a regular
 * expression built from the search query's search term.
 */
public class SearchRegexMemberVisitor extends AbstractMemberVisitor {

    /**
     * The compiled pattern members are matched against.
     */
    protected final Pattern pattern;

    /**
     * Constructs an instance using the query's search term as a regular expression.
     *
     * @param query the search query
     */
    public SearchRegexMemberVisitor(MemberSearchQuery query) {
        this(query, Pattern.compile(query.getSearchTerm(), query.isCaseSensitive() ? Pattern.CASE_INSENSITIVE : 0));
    }

    /**
     * Constructs an instance using the given precompiled pattern.
     *
     * @param query the search query
     * @param pattern the pattern to match member names/aliases against
     */
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