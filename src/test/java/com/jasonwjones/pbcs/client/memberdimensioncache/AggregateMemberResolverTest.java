package com.jasonwjones.pbcs.client.memberdimensioncache;

import com.jasonwjones.pbcs.client.PbcsMember;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class AggregateMemberResolverTest {

    @Test
    public void getAliasReturnsFirstNonNullResultInDelegateOrder() {
        RecordingResolver first = new RecordingResolver();
        RecordingResolver second = new RecordingResolver();
        second.aliases.put("Actual:Alias2", "Second Alias");
        AggregateMemberResolver aggregate = new AggregateMemberResolver(first, second);

        String alias = aggregate.getAlias(null, "Actual", "Alias2");

        assertThat(alias, is("Second Alias"));
    }

    @Test
    public void getAliasReturnsNullWhenNoDelegateHasIt() {
        AggregateMemberResolver aggregate = new AggregateMemberResolver(new RecordingResolver(), new RecordingResolver());

        assertThat(aggregate.getAlias(null, "Actual", "Alias2"), is(nullValue()));
    }

    @Test
    public void setAliasPropagatesToEveryDelegate() {
        RecordingResolver first = new RecordingResolver();
        RecordingResolver second = new RecordingResolver();
        AggregateMemberResolver aggregate = new AggregateMemberResolver(first, second);

        aggregate.setAlias(null, "Actual", "Alias2", "Actual Alias");

        assertThat(first.aliases.get("Actual:Alias2"), is("Actual Alias"));
        assertThat(second.aliases.get("Actual:Alias2"), is("Actual Alias"));
    }

    private static class RecordingResolver implements PbcsPlanType.MemberResolver {

        private final Map<String, String> aliases = new HashMap<>();

        @Override
        public PbcsMember getMember(PbcsPlanType planType, String memberOrAliasName) {
            return null;
        }

        @Override
        public void setMember(PbcsPlanType planType, String resolvedName, PbcsMember member) {
        }

        @Override
        public String getAlias(PbcsPlanType planType, String memberName, String aliasTableName) {
            return aliases.get(memberName + ":" + aliasTableName);
        }

        @Override
        public void setAlias(PbcsPlanType planType, String memberName, String aliasTableName, String alias) {
            aliases.put(memberName + ":" + aliasTableName, alias);
        }

    }

}
