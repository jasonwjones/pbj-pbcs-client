package com.jasonwjones.pbcs.client.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jasonwjones.pbcs.api.v3.SubstitutionVariable;
import com.jasonwjones.pbcs.client.PbcsApplicationSubstitutionVariable;
import com.jasonwjones.pbcs.client.PbcsObjectType;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.client.PbcsPlanTypeSubstitutionVariable;
import com.jasonwjones.pbcs.client.PbcsSubstitutionVariable;
import com.jasonwjones.pbcs.client.PbcsSubstitutionVariableScope;
import com.jasonwjones.pbcs.client.exceptions.PbcsNoSuchObjectException;
import com.jasonwjones.pbcs.client.exceptions.PbcsNoSuchVariableException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;

/**
 * Substitution variable reads and writes against canned responses, in the shapes the REST API uses: the
 * application's list marks each variable <code>ALL</code> or with its plan type's name, and a plan type's list
 * holds only that plan type's variables.
 */
public class PbcsSubstitutionVariablesTest {

    private static final String APPLICATION_ENDPOINT = "applications/Vision/substitutionvariables";

    private static final String PLAN_TYPE_ENDPOINT = "applications/Vision/plantypes/Plan1/substitutionvariables";

    private static final String APPLICATION_VARIABLES = "{\"items\":["
            + "{\"name\":\"CurYr\",\"value\":\"FY24\",\"planType\":\"ALL\"},"
            + "{\"name\":\"Plan1Yr\",\"value\":\"FY25\",\"planType\":\"Plan1\"}]}";

    private static final String PLAN_TYPE_VARIABLES = "{\"items\":[{\"name\":\"Plan1Yr\",\"value\":\"FY25\",\"planType\":\"Plan1\"}]}";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final StubRestServer server = new StubRestServer();

    @Test
    public void applicationVariablesKeepTheScopeTheRestApiGives() {
        server.respond(APPLICATION_ENDPOINT, APPLICATION_VARIABLES);

        List<PbcsSubstitutionVariable> variables = server.application().getVariables();

        assertThat(variables, hasSize(2));
        PbcsSubstitutionVariable curYr = variables.get(0);
        assertThat(curYr.getScope(), is(PbcsSubstitutionVariableScope.APPLICATION));
        assertThat(curYr, instanceOf(PbcsApplicationSubstitutionVariable.class));
        assertThat(curYr, not(instanceOf(PbcsPlanTypeSubstitutionVariable.class)));
        assertThat(curYr.getValue(), is("FY24"));
        assertThat(curYr.getQualifiedName(), is("Vision.CurYr"));
        assertThat(curYr.getObjectType(), is(PbcsObjectType.SUBSTITUTION_VARIABLE));

        PbcsSubstitutionVariable plan1Yr = variables.get(1);
        assertThat(plan1Yr.getScope(), is(PbcsSubstitutionVariableScope.PLAN_TYPE));
        assertThat(((PbcsPlanTypeSubstitutionVariable) plan1Yr).getPlanTypeName(), is("Plan1"));
        assertThat(plan1Yr.getQualifiedName(), is("Vision.Plan1.Plan1Yr"));
        assertThat(plan1Yr.toString(), is("Vision.Plan1.Plan1Yr=FY25"));

        assertThat("naming a plan type's variable doesn't look the plan type up", server.paths, contains(APPLICATION_ENDPOINT));
    }

    @Test
    public void aPlanTypeIsLookedUpOnceWhenFirstAskedFor() {
        server.respond(APPLICATION_ENDPOINT, APPLICATION_VARIABLES);
        PbcsPlanTypeImpl plan1 = server.planType();
        List<String> lookups = new ArrayList<>();
        PbcsApplicationImpl application = new PbcsApplicationImpl(server.context(), null, StubRestServer.applicationDetails()) {
            @Override
            public PbcsPlanType getPlanType(String planTypeName) {
                lookups.add(planTypeName);
                return plan1;
            }
        };

        PbcsPlanTypeSubstitutionVariable plan1Yr = (PbcsPlanTypeSubstitutionVariable) application.getVariables().get(1);

        assertThat(lookups, hasSize(0));
        assertThat(plan1Yr.getPlanType(), sameInstance(plan1));
        assertThat(plan1Yr.getParent(), sameInstance(plan1));
        assertThat(plan1Yr.withValue("FY26").getPlanType(), sameInstance(plan1));
        assertThat(lookups, contains("Plan1"));
    }

    @Test
    public void getVariableOnTheApplicationOnlyFindsApplicationScopedVariables() {
        server.respond(APPLICATION_ENDPOINT, "{\"items\":["
                + "{\"name\":\"CurYr\",\"value\":\"FY23\",\"planType\":\"Plan1\"},"
                + "{\"name\":\"CurYr\",\"value\":\"FY24\",\"planType\":\"ALL\"}]}");

        PbcsApplicationSubstitutionVariable curYr = server.application().getVariable("CurYr");

        assertThat(curYr.getScope(), is(PbcsSubstitutionVariableScope.APPLICATION));
        assertThat(curYr.getValue(), is("FY24"));
    }

    @Test
    public void getVariableOnTheApplicationDoesNotFallBackToAPlanType() {
        server.respond(APPLICATION_ENDPOINT, PLAN_TYPE_VARIABLES);

        try {
            server.application().getVariable("Plan1Yr");
            throw new AssertionError("expected a PbcsNoSuchVariableException");
        } catch (PbcsNoSuchVariableException expected) {
            assertThat(expected.getMessage(), containsString("Plan1Yr"));
            assertThat(expected, instanceOf(PbcsNoSuchObjectException.class));
            assertThat(expected.getObjectName(), is("Plan1Yr"));
            assertThat(expected.getObjectType(), is(PbcsObjectType.SUBSTITUTION_VARIABLE));
        }
    }

    @Test
    public void planTypeVariablesComeFromThePlanTypesEndpoint() {
        server.respond(PLAN_TYPE_ENDPOINT, PLAN_TYPE_VARIABLES);
        PbcsPlanTypeImpl plan1 = server.planType();

        List<PbcsPlanTypeSubstitutionVariable> variables = plan1.getVariables();

        assertThat(variables, hasSize(1));
        PbcsPlanTypeSubstitutionVariable plan1Yr = variables.get(0);
        assertThat(plan1Yr.getName(), is("Plan1Yr"));
        assertThat(plan1Yr.getValue(), is("FY25"));
        assertThat(plan1Yr.getScope(), is(PbcsSubstitutionVariableScope.PLAN_TYPE));
        assertThat(plan1Yr.getPlanTypeName(), is("Plan1"));
        assertThat(plan1Yr.getPlanType(), sameInstance(plan1));
        assertThat(plan1Yr.getQualifiedName(), is("Vision.Plan1.Plan1Yr"));
        assertThat(server.paths, contains(PLAN_TYPE_ENDPOINT));
    }

    @Test
    public void getVariableOnAPlanTypeNamesThePlanTypeWhenMissing() {
        server.respond(PLAN_TYPE_ENDPOINT, PLAN_TYPE_VARIABLES);
        PbcsPlanTypeImpl plan1 = server.planType();

        assertThat(plan1.getVariable("Plan1Yr").getValue(), is("FY25"));
        try {
            plan1.getVariable("CurYr");
            throw new AssertionError("expected a PbcsNoSuchVariableException");
        } catch (PbcsNoSuchVariableException expected) {
            assertThat(expected.getMessage(), containsString("Plan type Plan1"));
            assertThat(expected.getObjectName(), is("CurYr"));
        }
    }

    @Test
    public void withValueLeavesTheOriginalAlone() {
        PbcsApplicationImpl application = server.application();
        PbcsApplicationSubstitutionVariable curYr = new PbcsApplicationSubstitutionVariableImpl(application, "CurYr", "FY24");

        PbcsApplicationSubstitutionVariable next = curYr.withValue("FY25");

        assertThat(curYr.getValue(), is("FY24"));
        assertThat(next.getValue(), is("FY25"));
        assertThat(next.getApplication(), sameInstance(application));
        assertThat(next, is(curYr.withValue("FY25")));
        assertThat(next, not(curYr));

        PbcsPlanTypeSubstitutionVariable plan1Yr = new PbcsPlanTypeSubstitutionVariableImpl(application, "Plan1", "CurYr", "FY24");
        assertThat(plan1Yr.withValue("FY25").getPlanTypeName(), is("Plan1"));
        assertThat("the same name and value at another scope is another variable", plan1Yr, not((PbcsSubstitutionVariable) curYr));
        assertThat(server.paths, hasSize(0));
    }

    @Test
    public void updateVariablesSendsEachVariableAtItsScope() throws Exception {
        server.respond(APPLICATION_ENDPOINT, "{}");
        PbcsApplicationImpl application = server.application();

        application.updateVariables(Arrays.asList(
                new PbcsApplicationSubstitutionVariableImpl(application, "CurYr", "FY25"),
                new PbcsPlanTypeSubstitutionVariableImpl(application, "Plan1", "Plan1Yr", "FY26")));

        assertThat(server.paths, contains(APPLICATION_ENDPOINT));
        JsonNode items = objectMapper.readTree(server.bodies.get(0)).get("items");
        assertThat(items.toString(), is("[{\"name\":\"CurYr\",\"value\":\"FY25\",\"planType\":\"ALL\"},"
                + "{\"name\":\"Plan1Yr\",\"value\":\"FY26\",\"planType\":\"Plan1\"}]"));
    }

    @Test
    public void updateVariableOnTheApplicationIsApplicationScoped() throws Exception {
        server.respond(APPLICATION_ENDPOINT, "{}");

        server.application().updateVariable("CurYr", "FY25");

        JsonNode item = objectMapper.readTree(server.bodies.get(0)).get("items").get(0);
        assertThat(item.get("planType").asText(), is("ALL"));
    }

    @Test
    public void updateVariableOnAPlanTypeGoesThroughTheApplicationsEndpoint() throws Exception {
        server.respond(APPLICATION_ENDPOINT, "{}");

        server.planType().updateVariable("Plan1Yr", "FY26");

        assertThat(server.paths, contains(APPLICATION_ENDPOINT));
        JsonNode item = objectMapper.readTree(server.bodies.get(0)).get("items").get(0);
        assertThat(item.get("name").asText(), is("Plan1Yr"));
        assertThat(item.get("value").asText(), is("FY26"));
        assertThat(item.get("planType").asText(), is("Plan1"));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void theDeprecatedMethodsStillWork() throws Exception {
        server.respond(APPLICATION_ENDPOINT, APPLICATION_VARIABLES);
        server.respond(PLAN_TYPE_ENDPOINT, PLAN_TYPE_VARIABLES);
        PbcsApplicationImpl application = server.application();

        Set<SubstitutionVariable> all = application.getSubstitutionVariables();
        assertThat(all, containsInAnyOrder(new SubstitutionVariable("CurYr", "FY24"), plan1Yr("FY25")));
        assertThat(application.getSubstitutionVariable("CurYr").getValue(), is("FY24"));
        assertThat(server.planType().getSubstitutionVariables(), contains(plan1Yr("FY25")));

        application.updateSubstitutionVariable("CurYr", "FY25");
        JsonNode item = objectMapper.readTree(server.bodies.get(server.bodies.size() - 1)).get("items").get(0);
        assertThat(item.get("planType").asText(), is("ALL"));
    }

    @SuppressWarnings("deprecation")
    private static SubstitutionVariable plan1Yr(String value) {
        SubstitutionVariable variable = new SubstitutionVariable("Plan1Yr", value);
        variable.setPlanType("Plan1");
        return variable;
    }

}
