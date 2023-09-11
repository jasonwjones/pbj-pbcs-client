package com.jasonwjones.pbcs.client.impl.membervisitors;

import com.jasonwjones.pbcs.client.PbcsMemberProperties;

import java.nio.file.FileVisitResult;
import java.util.regex.Pattern;

public class SearchRegexMemberVisitor extends AbstractMemberVisitor {

    private final Pattern pattern;

    public SearchRegexMemberVisitor(String regex) {
        this.pattern = Pattern.compile(regex);
    }

    @Override
    public FileVisitResult preVisitMember(PbcsMemberProperties member) {
        if (pattern.matcher(member.getName()).matches()) {
            addMember(member);
        }
        return FileVisitResult.CONTINUE;
    }

}