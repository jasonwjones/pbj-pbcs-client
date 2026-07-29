package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsMember;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import org.junit.Test;

import java.lang.reflect.Proxy;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class PlanTypeConfigurationImplTest {

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
    public void cacheMemberStoresNameAndAliasByDefault() {
        RecordingMemberResolver resolver = new RecordingMemberResolver();
        PbcsMember member = member("USD", "US Dollar");

        int cachedNames = PbcsExplicitDimensionsPlanTypeImpl.cacheMember(resolver, null, member, false);

        assertThat(cachedNames, is(2));
        assertThat(resolver.members, hasKey("USD"));
        assertThat(resolver.members, hasKey("US Dollar"));
    }

    @Test
    public void cacheMemberOmitsAliasWhenConfigured() {
        RecordingMemberResolver resolver = new RecordingMemberResolver();
        PbcsMember member = member("USD", "US Dollar");

        int cachedNames = PbcsExplicitDimensionsPlanTypeImpl.cacheMember(resolver, null, member, true);

        assertThat(cachedNames, is(1));
        assertThat(resolver.members, hasKey("USD"));
        assertThat(resolver.members, not(hasKey("US Dollar")));
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
