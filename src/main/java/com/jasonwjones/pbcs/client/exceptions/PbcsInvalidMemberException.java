package com.jasonwjones.pbcs.client.exceptions;

import com.jasonwjones.pbcs.client.PbcsObjectType;

public class PbcsInvalidMemberException extends PbcsNoSuchObjectException {

    private final String invalidMember;

    public PbcsInvalidMemberException(String memberName) {
        super(memberName, PbcsObjectType.MEMBER);
        this.invalidMember = memberName;
    }

    public String getInvalidMember() {
        return invalidMember;
    }

}