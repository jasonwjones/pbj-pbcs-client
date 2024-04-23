package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.client.PbcsMemberProperties;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.client.impl.PlanTypeConfigurationImpl;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class VisionPlanExplicitMembersIT extends VisionCubeIT {

    private static final String INVALID_MEMBER = "XXX_Q1";

    @Test
    public void getDimensions() {
        assertThat(cube.getDimensions().size(), is(DIMENSIONS.size()));
    }

    @Test
    public void getValidMember() {
        cube = cube();
        PbcsMemberProperties jan = cube.getMemberOrAlias("Jan");
        assertThat(jan.getDimensionName(), is("Period"));
    }

    @Test
    public void getValidMemberViaAlias() {
        cube = cube();
        PbcsMemberProperties jan = cube.getMemberOrAlias("NI");
        assertThat(jan.getDimensionName(), is("Account"));
    }

    @Test
    public void getInvalidMember() {
        cube = cube();
        PbcsMemberProperties jan = cube.getMemberOrAlias(INVALID_MEMBER);
        assertThat(jan, is(nullValue()));
    }

    private PbcsPlanType cube() {
        PlanTypeConfigurationImpl configuration = new PlanTypeConfigurationImpl();
        configuration.setName("Plan1");
        configuration.setSkipCheck(true);
        configuration.setExplicitDimensions(DIMENSIONS);
        configuration.setMemberSearchThreads(8);
        return app.getPlanType(configuration);
    }

}