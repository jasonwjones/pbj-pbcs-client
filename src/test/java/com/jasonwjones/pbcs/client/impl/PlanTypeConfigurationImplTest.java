package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsMember;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import org.junit.Test;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class PlanTypeConfigurationImplTest {

    @Test
    public void defaultAliasTableIsAlwaysKnown() {
        PbcsApplication.PlanTypeConfiguration configuration =
                new PlanTypeConfigurationImpl.Builder("Plan1").build();

        assertThat(configuration.getAliasTables(), contains("Default"));
    }

    @Test
    public void builderAddsKnownAliasTablesWithoutDuplicatingDefault() {
        PbcsApplication.PlanTypeConfiguration configuration =
                new PlanTypeConfigurationImpl.Builder("Plan1")
                        .aliasTables(Arrays.asList("Alias2", "default", " French "))
                        .build();

        assertThat(configuration.getAliasTables(), contains("Default", "Alias2", "French"));
    }

    @Test
    public void ignoreAliasesIsDisabledByDefault() {
        PbcsApplication.PlanTypeConfiguration configuration =
                new PlanTypeConfigurationImpl.Builder("Plan1").build();

        assertThat(configuration.isIgnoreAliases(), is(false));
    }

    @Test
    public void builderCanEnableIgnoreAliases() {
        PbcsApplication.PlanTypeConfiguration configuration =
                new PlanTypeConfigurationImpl.Builder("Plan1")
                        .ignoreAliases()
                        .build();

        assertThat(configuration.isIgnoreAliases(), is(true));
    }

    @Test
    public void discoverDimensionsIsDisabledByDefault() {
        PbcsApplication.PlanTypeConfiguration configuration =
                new PlanTypeConfigurationImpl.Builder("Plan1").build();

        assertThat(configuration.isDiscoverDimensions(), is(false));
    }

    @Test
    public void builderCanEnableDiscoverDimensions() {
        PbcsApplication.PlanTypeConfiguration configuration =
                new PlanTypeConfigurationImpl.Builder("Plan1")
                        .discoverDimensions()
                        .build();

        assertThat(configuration.isDiscoverDimensions(), is(true));
    }

    @Test
    public void cacheMemberStoresNameAndAliasByDefault() {
        RecordingMemberResolver resolver = new RecordingMemberResolver();
        PbcsMember member = member("USD", "US Dollar");
        PbcsPlanType plan = planType(List.of("Default"), Map.of());

        int cachedNames = PbcsExplicitDimensionsPlanTypeImpl.cacheMember(resolver, plan, member, false);

        assertThat(cachedNames, is(2));
        assertThat(resolver.members, hasKey("USD"));
        assertThat(resolver.members, hasKey("US Dollar"));
    }

    @Test
    public void cacheMemberOmitsAliasWhenConfigured() {
        RecordingMemberResolver resolver = new RecordingMemberResolver();
        PbcsMember member = member("USD", "US Dollar");
        PbcsPlanType plan = planType(List.of("Default"), Map.of());

        int cachedNames = PbcsExplicitDimensionsPlanTypeImpl.cacheMember(resolver, plan, member, true);

        assertThat(cachedNames, is(1));
        assertThat(resolver.members, hasKey("USD"));
        assertThat(resolver.members, not(hasKey("US Dollar")));
    }

    /**
     * Every configured table's alias, not just Default's.
     *
     * <p>Bulk caching used to read {@code member.getAlias()} and stop, so a walk of the outline left
     * every non-Default alias unresolvable - the cache a caller had just paid to build did not contain
     * the names they were about to look up.
     */
    @Test
    public void cacheMemberStoresAliasesFromEveryConfiguredTable() {
        RecordingMemberResolver resolver = new RecordingMemberResolver();
        PbcsMember member = member("USD", "US Dollar");
        PbcsPlanType plan = planType(List.of("Default", "English", "SomeTable"),
                Map.of("English", "Dollar", "SomeTable", "Greenback"));

        int cachedNames = PbcsExplicitDimensionsPlanTypeImpl.cacheMember(resolver, plan, member, false);

        assertThat(cachedNames, is(4));
        assertThat(resolver.members, hasKey("USD"));
        assertThat(resolver.members, hasKey("US Dollar"));
        assertThat(resolver.members, hasKey("Dollar"));
        assertThat(resolver.members, hasKey("Greenback"));
    }

    /** An alias repeated across tables, or equal to the member's own name, is cached once. */
    @Test
    public void cacheMemberDoesNotRepeatAnAlias() {
        RecordingMemberResolver resolver = new RecordingMemberResolver();
        PbcsMember member = member("USD", "US Dollar");
        PbcsPlanType plan = planType(List.of("Default", "English", "SomeTable"),
                Map.of("English", "US Dollar", "SomeTable", "USD"));

        int cachedNames = PbcsExplicitDimensionsPlanTypeImpl.cacheMember(resolver, plan, member, false);

        assertThat(cachedNames, is(2));
        assertThat(resolver.members, hasKey("USD"));
        assertThat(resolver.members, hasKey("US Dollar"));
    }

    @Test
    public void toStringHandlesNullMemberResolver() {
        PlanTypeConfigurationImpl configuration = new PlanTypeConfigurationImpl();
        configuration.setMemberResolver(null);

        assertThat(configuration.toString(), containsString("memberResolver=null"));
    }

    private static PbcsMember member(String name, String alias) {
        return (PbcsMember) Proxy.newProxyInstance(
                PbcsMember.class.getClassLoader(),
                new Class<?>[] {PbcsMember.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "getName" -> name;
                    case "getAlias" -> alias;
                    case "getDimensionName" -> "Account";
                    default -> throw new UnsupportedOperationException(method.getName());
                });
    }

    /**
     * A plan that knows the given alias tables and answers one alias out of one of them.
     *
     * @param aliasTables what the plan was configured with, Default included or not
     * @param aliases     alias table to the alias this plan's member has in it
     */
    private static PbcsPlanType planType(List<String> aliasTables, Map<String, String> aliases) {
        return (PbcsPlanType) Proxy.newProxyInstance(
                PbcsPlanType.class.getClassLoader(),
                new Class<?>[] {PbcsPlanType.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "getAliasTables" -> aliasTables;
                    case "getMemberAlias" -> aliases.get((String) args[2]);
                    default -> throw new UnsupportedOperationException(method.getName());
                });
    }

    private static class RecordingMemberResolver implements PbcsPlanType.MemberResolver {

        private final Map<String, PbcsMember> members = new LinkedHashMap<>();

        @Override
        public PbcsMember getMember(PbcsPlanType planType, String memberOrAliasName) {
            return members.get(memberOrAliasName);
        }

        @Override
        public void setMember(PbcsPlanType planType, String resolvedName, PbcsMember member) {
            members.put(resolvedName, member);
        }

    }

}
