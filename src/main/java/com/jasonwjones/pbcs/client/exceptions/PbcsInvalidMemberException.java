package com.jasonwjones.pbcs.client.exceptions;

import com.jasonwjones.pbcs.client.PbcsObjectType;

public class PbcsInvalidMemberException extends PbcsNoSuchObjectException {

    public PbcsInvalidMemberException(String memberName) {
        super(memberName, PbcsObjectType.MEMBER);
    }

}