package com.jasonwjones.pbcs.client;

/**
 * Models different types of queries that can be performed by a {@link PbcsPlanType#queryMembers(String, PbcsMemberQueryType)}
 * operation.
 */
public enum PbcsMemberQueryType {

    /**
     * The query results will include the base member and its children.
     */
    ICHILDREN,

    /**
     * The query results will include only the children of the base member.
     */
    CHILDREN,

    /**
     * The query results will include the base member and all its descendants.
     */
    IDESCENDANTS,

    /**
     * The query results will include only the descendants of the base member.
     */
    DESCENDANTS,

    /**
     * The query results will include the base member and all its ancestors.
     */
    IANCESTORS,

    /**
     * The query results will include only the ancestors of the base member.
     */
    ANCESTORS,

    /**
     * The query results will include the base memebr and all its siblings.
     */
    ISIBLINGS,

    /**
     * The query results will include only the siblings of the base member.
     */
    SIBLINGS;

    /**
     * Check if this query type includes the base query member in the results.
     * @return true if the base member should be included, false otherwise
     */
    public boolean isIncludeOriginalMember() {
        return this == ICHILDREN || this == IDESCENDANTS || this == IANCESTORS || this == ISIBLINGS;
    }

}