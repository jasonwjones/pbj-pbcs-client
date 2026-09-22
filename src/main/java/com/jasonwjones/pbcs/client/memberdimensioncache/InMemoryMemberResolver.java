package com.jasonwjones.pbcs.client.memberdimensioncache;

import com.jasonwjones.pbcs.client.PbcsMember;
import com.jasonwjones.pbcs.client.PbcsPlanType;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Keeps resolved members and aliases in a {@link ConcurrentMap}, for the life of the plan type.
 *
 * <p>The default until now resolved nothing and remembered nothing, which made two ordinary things
 * expensive. Resolving a member searches the plan's dimensions, so asking twice searched twice. Worse,
 * {@link PbcsPlanType#getMemberAlias(String, String, String)} answers by fetching the whole
 * dimension's alias tree and offering every entry to the resolver - so with a resolver that discarded
 * them, one member's alias cost one dimension's worth of download, every time it was asked for.
 *
 * <p>Held per plan type rather than statically, so it lives as long as the plan a caller is holding
 * and no longer. Nothing invalidates: an outline edited underneath a plan that has already resolved a
 * member will not be noticed, which is the trade every cache here makes and the reason a caller who
 * needs to see such a change opens the plan again.
 */
public class InMemoryMemberResolver implements PbcsPlanType.MemberResolver {

    private final ConcurrentMap<String, PbcsMember> members = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, String> dimensions = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, String> aliases = new ConcurrentHashMap<>();

    /**
     * Constructs an empty instance.
     */
    public InMemoryMemberResolver() {
    }

    @Override
    public PbcsMember getMember(PbcsPlanType planType, String memberOrAliasName) {
        return members.get(key(planType, memberOrAliasName));
    }

    @Override
    public void setMember(PbcsPlanType planType, String resolvedName, PbcsMember member) {
        if (member != null) {
            members.put(key(planType, resolvedName), member);
        }
    }

    @Override
    public String getDimensionName(PbcsPlanType planType, String memberName) {
        return dimensions.get(key(planType, memberName));
    }

    @Override
    public void setDimension(PbcsPlanType planType, String memberName, String dimensionName) {
        if (dimensionName != null) {
            dimensions.put(key(planType, memberName), dimensionName);
        }
    }

    @Override
    public String getAlias(PbcsPlanType planType, String memberName, String aliasTableName) {
        return aliases.get(aliasKey(planType, memberName, aliasTableName));
    }

    @Override
    public void setAlias(PbcsPlanType planType, String memberName, String aliasTableName, String alias) {
        if (alias != null) {
            aliases.put(aliasKey(planType, memberName, aliasTableName), alias);
        }
    }

    /**
     * Keyed by plan as well as name, although one of these normally serves one plan: the interface
     * hands the plan to every call precisely so an implementation can be shared, and one that ignored
     * it would return one cube's member for another's the day someone shared an instance.
     */
    private static String key(PbcsPlanType planType, String name) {
        return planType.getApplication().getName() + '.' + planType.getName() + '.' + name;
    }

    /** Aliases differ per table, so the table belongs in the key; null and "Default" are one table. */
    private static String aliasKey(PbcsPlanType planType, String memberName, String aliasTableName) {
        String table = com.jasonwjones.pbcs.client.PbcsMember.isDefaultAliasTable(aliasTableName)
                ? "Default" : aliasTableName;
        return key(planType, memberName) + '@' + table;
    }

}
