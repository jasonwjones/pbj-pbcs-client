package com.jasonwjones.pbcs.client;

/**
 * Defines a query to be executed by the {@link PbcsPlanType#searchMembers(MemberSearchQuery)} method.
 */
public interface MemberSearchQuery {

    /**
     * The search type to perform.
     *
     * @return the search type to perform
     */
    Type getType();

    /**
     * The member name or the starting member name (depending on the type of search)
     *
     * @return the member name
     */
    String getMemberName();

    /**
     * The name of the dimension to search. If none (null) then all dimensions will be searched.
     *
     * @return the search dimension
     */
    String getDimensionName();

    /**
     * The search term to use, when using <code>SEARCH</code>, <code>SEARCH_WILD</code>, or <code>REGEX</code> search types.
     *
     * @return the search term, if any
     */
    String getSearchTerm();

    /**
     * Different types of searches that can be performed.
     */
    enum Type {

        /**
         * Searches for the given member name and once found, stops searching.
         */
        SEARCH,

        /**
         * Searches for members using a wildcard search pattern that can contain one or more asterisks, such as searching
         * for "Ja*" or "*ry" or even "*an*a*y.
         */
        SEARCH_WILD,

        /**
         * Searches using a standard Java regex such as <code>P_1[2-4]0</code> to search for member names starting with
         * <code>P_1</code>, ending with <code>0</code>, and containing a single 2, 3, or 4 in between.
         */
        REGEX

    }

}