package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.client.PbcsMember;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.testing.ReadOnlyIntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;

/**
 * Verifies member alias resolution against a real, non-Default alias table. Requires the Vision tenant's Plan1
 * to have an alias table named "SomeTable" in which Account.RegularAccount is aliased to "SomeTestAlias".
 */
@Category(ReadOnlyIntegrationTest.class)
public class PbcsPlanTypeAliasVisionIT extends AbstractVisionIT {

    private static final String DIMENSION_NAME = "Account";

    private static final String ALIAS_TABLE = "SomeTable";

    private static final String MEMBER_NAME = "RegularAccount";

    private static final String EXPECTED_ALIAS = "SomeTestAlias";

    @Test
    public void getMemberAliasesIncludesTheKnownNonDefaultAlias() {
        PbcsPlanType plan = app.getPlanType(planTypeConfiguration);

        Map<String, String> aliases = plan.getMemberAliases(DIMENSION_NAME, ALIAS_TABLE);

        assertThat(aliases, hasEntry(MEMBER_NAME, EXPECTED_ALIAS));
    }

    @Test
    public void getMemberAliasResolvesTheSingleMemberAlias() {
        PbcsPlanType plan = app.getPlanType(planTypeConfiguration);

        String alias = plan.getMemberAlias(DIMENSION_NAME, MEMBER_NAME, ALIAS_TABLE);

        assertThat(alias, is(EXPECTED_ALIAS));
    }

    @Test
    public void planScopedMemberResolvesItsOwnAliasFromTheNonDefaultTable() {
        PbcsPlanType plan = app.getPlanType(planTypeConfiguration);

        PbcsMember member = plan.getMember(DIMENSION_NAME, MEMBER_NAME);

        assertThat(member.getAlias(ALIAS_TABLE), is(EXPECTED_ALIAS));
    }

}
