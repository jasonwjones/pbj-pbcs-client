package com.jasonwjones.pbcs.client.exceptions;

/**
 * Thrown when a {@link com.jasonwjones.pbcs.client.PbcsPlanType.MemberResolver} knows (or thinks it knows) that a given
 * member is invalid. This is meant to speed up queries and operations that would otherwise keep brute-force searching
 * for invalid members.
 */
public class PbcsKnownInvalidMemberException extends PbcsInvalidMemberException {

    /**
     * Constructs an instance for the given member name.
     *
     * @param memberName the known-invalid member name
     */
    public PbcsKnownInvalidMemberException(String memberName) {
        super(memberName);
    }

}