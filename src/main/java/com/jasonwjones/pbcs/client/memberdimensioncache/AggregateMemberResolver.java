package com.jasonwjones.pbcs.client.memberdimensioncache;

import com.jasonwjones.pbcs.client.PbcsMember;
import com.jasonwjones.pbcs.client.PbcsPlanType;

import java.util.Arrays;
import java.util.List;

/**
 * A {@link PbcsPlanType.MemberResolver} that delegates to a list of other resolvers, checking each in order
 * and returning the first non-null result. Updates (via {@link #setMember}, {@link #addInvalidMember}, and
 * {@link #setAlias}) are propagated to all the delegate resolvers.
 */
public class AggregateMemberResolver implements PbcsPlanType.MemberResolver {

    private final List<PbcsPlanType.MemberResolver> memberResolvers;

    /**
     * Constructs an instance that delegates to the given resolvers, in the order given.
     *
     * @param memberResolvers the delegate resolvers
     */
    public AggregateMemberResolver(PbcsPlanType.MemberResolver... memberResolvers) {
        this(Arrays.asList(memberResolvers));
    }

    /**
     * Constructs an instance that delegates to the given resolvers, in the order given.
     *
     * @param memberResolvers the delegate resolvers
     */
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

    @Override
    public String getAlias(PbcsPlanType planType, String memberName, String aliasTableName) {
        for (PbcsPlanType.MemberResolver memberResolver : memberResolvers) {
            String alias = memberResolver.getAlias(planType, memberName, aliasTableName);
            if (alias != null) {
                return alias;
            }
        }
        return null;
    }

    @Override
    public void setAlias(PbcsPlanType planType, String memberName, String aliasTableName, String alias) {
        for (PbcsPlanType.MemberResolver memberResolver : memberResolvers) {
            memberResolver.setAlias(planType, memberName, aliasTableName, alias);
        }
    }

}