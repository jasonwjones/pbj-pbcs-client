package com.jasonwjones.pbcs.client.impl.membervisitors;

import com.jasonwjones.pbcs.client.PbcsMemberProperties;
import com.jasonwjones.pbcs.client.impl.MemberVisitor;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMemberVisitor implements MemberVisitor {

    private List<PbcsMemberProperties> matchingMembers = new ArrayList<>();

    public List<PbcsMemberProperties> getMatchingMembers() {
        return matchingMembers;
    }

    protected void addMember(PbcsMemberProperties member) {
        matchingMembers.add(member);
    }

}