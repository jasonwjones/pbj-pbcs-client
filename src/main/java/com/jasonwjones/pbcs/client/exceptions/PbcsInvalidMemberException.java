package com.jasonwjones.pbcs.client.exceptions;

import com.jasonwjones.pbcs.client.PbcsObjectType;

/**
 * Thrown when a member name is invalid. Note that many methods may simply return null instead of throwing an exception.
 * Check the related documentation for specific use case information. Most notably, {@link com.jasonwjones.pbcs.client.PbcsExplicitDimensionsPlanType#getMemberOrAlias(String)}
 * will return <code>null</code> if it can't resolve a member.
 */
public class PbcsInvalidMemberException extends PbcsNoSuchObjectException {

    public PbcsInvalidMemberException(String memberName) {
        super(memberName, PbcsObjectType.MEMBER);
    }

}