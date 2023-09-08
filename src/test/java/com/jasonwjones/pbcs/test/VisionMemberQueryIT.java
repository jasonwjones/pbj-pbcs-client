package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.client.PbcsMemberProperties;
import com.jasonwjones.pbcs.client.PbcsMemberQueryType;
import com.jasonwjones.pbcs.client.PbcsObjectType;
import com.jasonwjones.pbcs.client.exceptions.PbcsNoSuchObjectException;
import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThrows;

public class VisionMemberQueryIT extends VisionCubeIT {

    private static final String INVALID_MEMBER = "XXX_Q1";

    @Test
    public void getDimensions() {
        assertThat(cube.getDimensions().size(), is(DIMENSIONS.size()));
    }

    @Test
    public void searchUsingInvalidMember() {
        PbcsNoSuchObjectException exception = assertThrows(PbcsNoSuchObjectException.class, () -> cube.queryMembers(INVALID_MEMBER, PbcsMemberQueryType.CHILDREN));
        assertThat(exception.getObjectName(), is(INVALID_MEMBER));
        assertThat(exception.getObjectType(), is(PbcsObjectType.MEMBER));
    }

    @Test
    public void searchForChildren() {
        List<PbcsMemberProperties> children = cube.queryMembers("Q1", PbcsMemberQueryType.CHILDREN);
        assertThat(children, hasSize(3));
    }

    @Test
    public void quarterSiblingsContainsAllIncludingMember() {
        assertThat(names(cube.queryMembers("Q1", PbcsMemberQueryType.ISIBLINGS)), contains("Q1", "Q2", "Q3", "Q4"));
    }

    @Test
    public void quarterSiblingsContainsAllExcludingMember() {
        assertThat(names(cube.queryMembers("Q1", PbcsMemberQueryType.SIBLINGS)), contains("Q2", "Q3", "Q4"));
    }


    private static List<String> names(Collection<PbcsMemberProperties> members) {
        return members.stream().map(PbcsMemberProperties::getName).collect(Collectors.toList());
    }

}