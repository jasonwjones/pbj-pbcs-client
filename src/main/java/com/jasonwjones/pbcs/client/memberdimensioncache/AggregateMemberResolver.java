package com.jasonwjones.pbcs.client.memberdimensioncache;

import com.jasonwjones.pbcs.client.PbcsMember;
import com.jasonwjones.pbcs.client.PbcsPlanType;

import java.util.Arrays;
import java.util.List;

public class AggregateMemberResolver implements PbcsPlanType.MemberResolver {

    private final List<PbcsPlanType.MemberResolver> memberResolvers;

    public AggregateMemberResolver(PbcsPlanType.MemberResolver... memberResolvers) {
        this(Arrays.asList(memberResolvers));
    }

    public AggregateMemberResolver(List<PbcsPlanType.MemberResolver> memberResolvers) {
        this.memberResolvers = memberResolvers;
    }

    @Override
    public PbcsMember getMember(PbcsPlanType planType, String memberOrAliasName) {
        for (PbcsPlanType.MemberResolver memberResolver : memberResolvers) {
            PbcsMember member = memberResolver.getMember(planType, memberOrAliasName);
            if (member != null) {
                return member;
            }
        }
        return null;
    }

    @Override
    public void setMember(PbcsPlanType planType, String resolvedName, PbcsMember member) {
        for (PbcsPlanType.MemberResolver memberResolver : memberResolvers) {
            memberResolver.setMember(planType, resolvedName, member);
        }
    }

    @Override
    public void addInvalidMember(PbcsPlanType planType, String invalidMemberOrAliasName) {
        for (PbcsPlanType.MemberResolver memberResolver : memberResolvers) {
            memberResolver.addInvalidMember(planType, invalidMemberOrAliasName);
        }
    }

}