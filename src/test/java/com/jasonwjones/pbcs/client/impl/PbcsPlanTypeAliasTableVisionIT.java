package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.client.PbcsMember;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Alias tables other than Default, which Cloud EPM treats quite differently from the Default one.
 *
 * <p>Two things were in the way of a grid that names a member by such an alias, and only one of them
 * was ours. Established live against Vision:
 *
 * <ul>
 *   <li>The server resolves a <em>Default</em> alias in a grid definition by itself - a retrieve of
 *       {@code Average Salaries} works as well as one of {@code 9800} - and rejects an alias from any
 *       other table with "The member X does not exist for the specified cube". So a non-Default alias
 *       has to become its member before the request is built.</li>
 *   <li>PBJ could not have done that anyway: the only reverse lookup was
 *       {@link PbcsMember#searchForDescendant(String)} comparing {@code getAlias()}, which is the
 *       Default table by definition.</li>
 * </ul>
 *
 * <p>Alias tables are not enumerable through the REST API, so the ones under test are the ones named
 * here, matching what the Vision pod actually has.
 */
public class PbcsPlanTypeAliasTableVisionIT extends AbstractVisionIT {

    /** An account carrying an alias in SomeTable and in no other table. */
    private static final String MEMBER = "RegularAccount";

    private static final String ALIAS = "SomeTestAlias";

    private static final String ALIAS_TABLE = "SomeTable";

    private PbcsPlanType planWithAliasTables() {
        planTypeConfiguration.setAliasTables(List.of(ALIAS_TABLE));
        return app.getPlanType(planTypeConfiguration);
    }

    @Test
    public void theServerServesAnAliasTableOtherThanDefault() {
        assertEquals(MEMBER + " should have an alias in " + ALIAS_TABLE,
                ALIAS, planWithAliasTables().getMemberAliases("Account", ALIAS_TABLE).get(MEMBER));
    }

    @Test
    public void anAliasFromAConfiguredTableResolvesToItsMember() {
        PbcsMember member = planWithAliasTables().getMemberOrAlias(ALIAS);

        assertNotNull("alias from a configured table did not resolve", member);
        assertEquals(MEMBER, member.getName());
    }

    /**
     * And a table nobody configured is not searched.
     *
     * <p>The point of the test is the cost, not the answer: a plan told about no alias tables must do
     * no extra work on a lookup that misses, or every failed member name in every grid would download
     * an outline. A null here is what "looked in nothing" looks like from outside.
     */
    @Test
    public void anAliasFromAnUnconfiguredTableIsNotFound() {
        assertNull(app.getPlanType(planTypeConfiguration).getMemberOrAlias(ALIAS));
    }

    /** Default keeps working the way it did, through the server rather than through us. */
    @Test
    public void aDefaultAliasStillResolves() {
        PbcsMember member = planWithAliasTables().getMemberOrAlias("Average Salaries");

        assertNotNull(member);
        assertEquals("9800", member.getName());
    }

    /**
     * A retrieve naming a member by a non-Default alias, which is what the whole thing is for.
     *
     * <p>Asserts against the same retrieve by member name rather than against a literal, because the
     * value at that intersection is Vision's business and may be empty - what matters is that the
     * server answers at all, where before it refused the request outright.
     */
    @Test
    public void aRetrieveCanNameAMemberByAConfiguredAlias() {
        PbcsPlanType plan = planWithAliasTables();

        assertEquals(plan.getCell(dataPoint(MEMBER)), plan.getCell(dataPoint(ALIAS)));
    }

    private static List<String> dataPoint(String account) {
        return List.of(account, "USD", "No Entity", "Jan", "No Product", "Actual", "Final", "FY09");
    }
}
