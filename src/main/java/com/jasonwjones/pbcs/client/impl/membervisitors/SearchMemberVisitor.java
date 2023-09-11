package com.jasonwjones.pbcs.client.impl.membervisitors;

import com.jasonwjones.pbcs.client.PbcsMemberProperties;

import java.nio.file.FileVisitResult;

public class SearchMemberVisitor extends AbstractMemberVisitor {

    private final String memberName;

    public SearchMemberVisitor(String memberName) {
        this.memberName = memberName;
    }

    @Override
    public FileVisitResult preVisitMember(PbcsMemberProperties member) {
        if (member.getName().equalsIgnoreCase(memberName)) {
            addMember(member);
            return FileVisitResult.TERMINATE;
        }
        return FileVisitResult.CONTINUE;
    }

}