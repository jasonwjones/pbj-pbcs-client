package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.client.PbcsDimension;
import com.jasonwjones.pbcs.client.PbcsMember;
import com.jasonwjones.pbcs.testing.ReadOnlyIntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@Category(ReadOnlyIntegrationTest.class)
public class PbcsExplicitDimensionsPlanTypeMemberIT extends AbstractVisionCubeIT{

    private static final String INVALID_MEMBER = "XXX_Q1";

    @Test
    public void whenGetMaxGeneration() {
        PbcsDimension dimension = cube.getDimension("Period");
        PbcsMember root = dimension.getRoot();
        assertThat(root.getGeneration(), is(1));
        // Period -> YearTotal -> Q1 -> Jan = 4
        assertThat(root.getMaxGeneration(), is(4));
    }

    @Test
    public void whenGetLevel() {
        PbcsDimension dimension = cube.getDimension("Period");
        PbcsMember root = dimension.getRoot();
        assertThat(root.getLevel(), is(3));
    }

    @Test
    public void whenGetMember() {
        for (PbcsDimension dimension : cube.getDimensions()) {
            System.out.println("Dim: " + dimension.getName());
            PbcsMember member = dimension.getRoot();
            assertThat(member.getName(), is(dimension.getName()));
            printMember(member, 0);
        }
    }

    @Test
    public void getDimensions() {
        assertThat(cube.getDimensions().size(), is(DIMENSIONS.size()));
    }

    @Test
    public void getValidMember() {
        PbcsMember jan = cube.getMemberOrAlias("Jan");
        assertThat(jan.getDimensionName(), is("Period"));
    }

    @Test
    public void getValidMemberViaAlias() {
        PbcsMember jan = cube.getMemberOrAlias("NI");
        assertThat(jan.getDimensionName(), is("Account"));
    }

    @Test
    public void getInvalidMember() {
        PbcsMember jan = cube.getMemberOrAlias(INVALID_MEMBER);
        assertThat(jan, is(nullValue()));
    }

    private static void printMember(PbcsMember member, int level) {
        System.out.print("    ".repeat(level));
        System.out.printf("%s (%s) %n", member.getName(), member.getDataStorage());

        for (PbcsMember child : member.getChildren()) {
            printMember(child, level + 1);
        }
    }

}
