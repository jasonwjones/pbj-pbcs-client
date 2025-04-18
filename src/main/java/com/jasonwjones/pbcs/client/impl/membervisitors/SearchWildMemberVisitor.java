package com.jasonwjones.pbcs.client.impl.membervisitors;

import com.jasonwjones.pbcs.client.MemberSearchQuery;

import java.util.regex.Pattern;

public class SearchWildMemberVisitor extends SearchRegexMemberVisitor {

    public SearchWildMemberVisitor(MemberSearchQuery query) {
        super(query.getSearchTerm().replace("*", ".*"), !query.isCaseSensitive() ? Pattern.CASE_INSENSITIVE : 0, query.isSearchAliases());
    }

}