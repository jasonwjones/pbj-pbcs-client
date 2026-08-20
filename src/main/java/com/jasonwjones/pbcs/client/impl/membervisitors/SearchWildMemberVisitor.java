package com.jasonwjones.pbcs.client.impl.membervisitors;

import com.jasonwjones.pbcs.client.MemberSearchQuery;

import java.util.regex.Pattern;

/**
 * A {@link SearchRegexMemberVisitor} that treats the search query's search term as a simple wildcard
 * pattern, where {@code *} matches any sequence of characters.
 */
public class SearchWildMemberVisitor extends SearchRegexMemberVisitor {

    /**
     * Constructs an instance using the query's search term as a wildcard pattern.
     *
     * @param query the search query
     */
    public SearchWildMemberVisitor(MemberSearchQuery query) {
        super(query, Pattern.compile(query.getSearchTerm().replace("*", ".*"),
                !query.isCaseSensitive() ? Pattern.CASE_INSENSITIVE : 0));
    }

}
