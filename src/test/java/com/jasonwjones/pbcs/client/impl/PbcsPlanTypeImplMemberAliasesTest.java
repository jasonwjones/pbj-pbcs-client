package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.api.v3.AliasedMember;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsMember;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import org.junit.Test;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;

public class PbcsPlanTypeImplMemberAliasesTest {

    private final RestContext context = new RestContext(null, null, null, null);

    @Test
    public void flattenOmitsMembersWithNoAliasInTheTable() {
        AliasedMember root = member("Scenario", null,
                member("Actual", null),
                member("Plan", null),
                member("Current", "SomeCurrent"));

        Map<String, String> aliases = planType().flattenMemberAliases(root);

        assertThat(aliases.size(), is(1));
        assertThat(aliases, hasEntry("Current", "SomeCurrent"));
    }

    @Test
    public void flattenOmitsAliasIdenticalToMemberName() {
        AliasedMember root = member("Scenario", null, member("Actual", "Actual"));

        Map<String, String> aliases = planType().flattenMemberAliases(root);

        assertThat(aliases.isEmpty(), is(true));
    }

    @Test
    public void flattenRecursesIntoGrandchildren() {
        AliasedMember grandchild = member("Grandchild", "Grandchild Alias");
        AliasedMember child = member("Child", null, grandchild);
        AliasedMember root = member("Account", null, child);

        Map<String, String> aliases = planType().flattenMemberAliases(root);

        assertThat(aliases, hasEntry("Grandchild", "Grandchild Alias"));
    }

    @Test
    public void flattenIncludesRootAliasWhenPresent() {
        AliasedMember root = member("Scenario", "Scenario Alias");

        Map<String, String> aliases = planType().flattenMemberAliases(root);

        assertThat(aliases, hasEntry("Scenario", "Scenario Alias"));
    }

    @Test
    public void getMemberAliasesWritesResultsThroughToTheConfiguredMemberResolver() {
        RecordingMemberResolver resolver = new RecordingMemberResolver();
        AliasedMember root = member("Scenario", null, member("Actual", "Actual Alias"));
        FixedTreePlanTypeImpl planType = new FixedTreePlanTypeImpl(context, application(), root, resolver);

        Map<String, String> aliases = planType.getMemberAliases("Scenario", "Alias2");

        assertThat(aliases, hasEntry("Actual", "Actual Alias"));
        assertThat(resolver.stored.get("Actual:Alias2"), is("Actual Alias"));
    }

    @Test
    public void getMemberAliasReturnsCachedValueWithoutFetchingWhenResolverHasIt() {
        RecordingMemberResolver resolver = new RecordingMemberResolver();
        resolver.stored.put("Actual:Alias2", "Cached Alias");
        FixedTreePlanTypeImpl planType = new FixedTreePlanTypeImpl(context, application(), null, resolver);

        String alias = planType.getMemberAlias("Scenario", "Actual", "Alias2");

        assertThat(alias, is("Cached Alias"));
        assertThat(planType.fetchCount, is(0));
    }

    @Test
    public void getMemberAliasFallsBackToFetchingOnCacheMiss() {
        RecordingMemberResolver resolver = new RecordingMemberResolver();
        AliasedMember root = member("Scenario", null, member("Actual", "Fetched Alias"));
        FixedTreePlanTypeImpl planType = new FixedTreePlanTypeImpl(context, application(), root, resolver);

        String alias = planType.getMemberAlias("Scenario", "Actual", "Alias2");

        assertThat(alias, is("Fetched Alias"));
        assertThat(planType.fetchCount, is(1));
        assertThat(resolver.stored.get("Actual:Alias2"), is("Fetched Alias"));
    }

    private PbcsPlanTypeImpl planType() {
        return new PbcsPlanTypeImpl(context, application(), new PlanTypeConfigurationImpl.Builder("Plan1").build());
    }

    private static class FixedTreePlanTypeImpl extends PbcsPlanTypeImpl {

        private final AliasedMember tree;

        private int fetchCount;

        FixedTreePlanTypeImpl(RestContext context, PbcsApplication application, AliasedMember tree, PbcsPlanType.MemberResolver memberResolver) {
            super(context, application, new PlanTypeConfigurationImpl.Builder("Plan1")
                    .memberResolver(memberResolver)
                    .build());
            this.tree = tree;
        }

        @Override
        protected AliasedMember fetchMemberAliasTree(String dimensionName, String aliasTableName) {
            fetchCount++;
            return tree;
        }

    }

    private static class RecordingMemberResolver implements PbcsPlanType.MemberResolver {

        private final Map<String, String> stored = new HashMap<>();

        @Override
        public PbcsMember getMember(PbcsPlanType planType, String memberOrAliasName) {
            return null;
        }

        @Override
        public void setMember(PbcsPlanType planType, String resolvedName, PbcsMember member) {
        }

        @Override
        public String getAlias(PbcsPlanType planType, String memberName, String aliasTableName) {
            return stored.get(memberName + ":" + aliasTableName);
        }

        @Override
        public void setAlias(PbcsPlanType planType, String memberName, String aliasTableName, String alias) {
            stored.put(memberName + ":" + aliasTableName, alias);
        }

    }

    private static PbcsApplication application() {
        return (PbcsApplication) Proxy.newProxyInstance(
                PbcsApplication.class.getClassLoader(),
                new Class<?>[] {PbcsApplication.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "getName" -> "Vision";
                    case "getParent" -> null;
                    default -> throw new UnsupportedOperationException(method.getName());
                });
    }

    private static AliasedMember member(String name, String alias, AliasedMember... children) {
        AliasedMember member = new AliasedMember();
        member.setName(name);
        member.setAlias(alias);
        if (children.length > 0) {
            member.setChildren(List.of(children));
        }
        return member;
    }

}
