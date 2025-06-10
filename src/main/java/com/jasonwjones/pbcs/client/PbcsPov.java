package com.jasonwjones.pbcs.client;

import java.util.Collection;
import java.util.List;

/**
 * Represents a POV (i.e., a single fully qualified cell address).
 */
public interface PbcsPov extends Iterable<PbcsMember> {

    /**
     * Returns a collection of all the members in this POV.
     *
     * @return the POV members
     */
    Collection<PbcsMember> getMembers();

    /**
     * Creates a new POV where the given member is resolved into a {@link PbcsMember}.
     * Essentially just calls {@link #member(PbcsMember)} but as a convenience, performs
     * member resolution.
     *
     * @param memberName the member name to add/update to the POV
     * @return a new POV with the updated member
     */
    PbcsPov member(String memberName);

    /**
     * Creates a new POV using the given member.
     *
     * @param member the member to add/update to the POV
     * @return a new POV with the updated member
     */
    PbcsPov member(PbcsMember member);

    /**
     * Creates a new POV by removing the member/dimension with the given name.
     *
     * @param dimension the dimension to remove from the POV
     * @return a new POV without the given dimension
     */
    PbcsPov without(String dimension);

    /**
     * Convenience method that returns the names of members in the POV only.
     *
     * @return member names in the POV
     */
    List<String> memberNames();

}