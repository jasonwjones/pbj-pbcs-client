package com.jasonwjones.pbcs.client.impl.membervisitors;

import com.jasonwjones.pbcs.client.PbcsMember;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.util.PlanTypeWalker;

import java.util.regex.Pattern;

public class SearchRegexMemberVisitor extends AbstractMemberVisitor {

    private final Pattern pattern;

    public SearchRegexMemberVisitor(String regex) {
        this.pattern = Pattern.compile(regex);
    }

    @Override
    public PlanTypeWalker.MemberVisitResult visitMember(PbcsPlanType planType, PbcsMember member) {
        if (pattern.matcher(member.getName()).matches()) {
            addMember(member);
        }
        return PlanTypeWalker.MemberVisitResult.CONTINUE;
    }

}