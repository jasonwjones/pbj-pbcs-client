package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.api.v3.PbcsMemberPropertiesImpl;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsMember;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.client.exceptions.PbcsInvalidMemberException;
import org.junit.Test;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;

public class PbcsPlanTypeMemberImplTest {

    private final RestContext context = new RestContext(null, null, null, null);

    @Test
    public void applicationMemberRetainsChildrenFromEveryPlan() {
        PbcsMember member = new PbcsMemberImpl(context, application(Collections.emptyMap()), hierarchy());

        assertThat(childNames(member), contains("Plan1 Child", "Plan2 Child", "Unscoped Child"));
    }

    @Test
    public void planMemberFiltersChildrenAndPreservesPlanScopeRecursively() {
        PbcsMember member = planType(application(Collections.emptyMap())).getMember("Account", "Account");

        assertThat(member, instanceOf(PbcsPlanTypeMemberImpl.class));
        assertThat(childNames(member), contains("Plan1 Child", "Unscoped Child"));
        assertThat(member.getChildren().get(0), instanceOf(PbcsPlanTypeMemberImpl.class));
        assertThat(childNames(member.getChildren().get(0)), contains("Plan1 Grandchild"));
        assertThat(member.getLevel(), is(2));
        assertThat(member.searchForDescendant("Plan2 Grandchild"), is((PbcsMember) null));
    }

    @Test
    public void planMemberTreatsEmptyUsedInAsUnscoped() {
        PbcsMemberPropertiesImpl child = member("Empty UsedIn", Collections.emptyList());
        PbcsMemberPropertiesImpl root = member("Account", List.of("Plan1"), child);
        PbcsMember member = planType(application(Map.of("Account", root))).getMember("Account", "Account");

        assertThat(childNames(member), contains("Empty UsedIn"));
    }

    @Test
    public void filteredChildrenDriveLeafAndLevelSemantics() {
        PbcsMemberPropertiesImpl root = member("Account", List.of("Plan1"),
                member("Plan2 Child", List.of("Plan2")));
        PbcsMember member = planType(application(Map.of("Account", root))).getMember("Account", "Account");

        assertThat(member.isLeaf(), is(true));
        assertThat(member.getLevel(), is(0));
    }

    @Test
    public void directLookupRejectsMemberAssignedOnlyToAnotherPlan() {
        PbcsMemberPropertiesImpl member = member("Plan2 Child", List.of("Plan2"));
        PbcsPlanType planType = planType(application(Map.of("Plan2 Child", member)));

        assertThrows(PbcsInvalidMemberException.class, () -> planType.getMember("Account", "Plan2 Child"));
    }

    @Test
    public void parentLookupRemainsPlanScoped() {
        PbcsMemberPropertiesImpl parent = member("Account", List.of("Plan1"),
                member("Plan1 Child", List.of("Plan1")),
                member("Plan2 Child", List.of("Plan2")));
        PbcsMemberPropertiesImpl child = member("Plan1 Child", List.of("Plan1"));
        child.setParentName("Account");
        PbcsPlanType planType = planType(application(Map.of("Account", parent, "Plan1 Child", child)));

        PbcsMember resolvedParent = planType.getMember("Account", "Plan1 Child").getParentMember();

        assertThat(resolvedParent, instanceOf(PbcsPlanTypeMemberImpl.class));
        assertThat(childNames(resolvedParent), contains("Plan1 Child"));
    }

    private PbcsPlanType planType(PbcsApplication application) {
        return new PbcsPlanTypeImpl(context, application, new PlanTypeConfigurationImpl.Builder("Plan1").build());
    }

    private PbcsApplication application(Map<String, PbcsMemberPropertiesImpl> members) {
        Map<String, PbcsMemberPropertiesImpl> availableMembers = new LinkedHashMap<>(members);
        availableMembers.putIfAbsent("Account", hierarchy());
        return (PbcsApplication) Proxy.newProxyInstance(
                PbcsApplication.class.getClassLoader(),
                new Class<?>[] {PbcsApplication.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "getMember" -> new PbcsMemberImpl(context, (PbcsApplication) proxy, availableMembers.get(args[1]));
                    case "getName" -> "Vision";
                    case "getParent" -> null;
                    default -> throw new UnsupportedOperationException(method.getName());
                });
    }

    private static PbcsMemberPropertiesImpl hierarchy() {
        PbcsMemberPropertiesImpl plan1Child = member("Plan1 Child", List.of("Plan1"),
                member("Plan1 Grandchild", List.of("Plan1")),
                member("Plan2 Grandchild", List.of("Plan2")));
        return member("Account", Arrays.asList("Plan1", "Plan2"),
                plan1Child,
                member("Plan2 Child", List.of("Plan2")),
                member("Unscoped Child", null));
    }

    private static PbcsMemberPropertiesImpl member(String name, List<String> usedIn, PbcsMemberPropertiesImpl... children) {
        PbcsMemberPropertiesImpl member = new PbcsMemberPropertiesImpl();
        member.setName(name);
        member.setDimensionName("Account");
        member.setDataStorage("Store Data");
        member.setUsedIn(usedIn);
        member.setChildren(Arrays.asList(children));
        return member;
    }

    private static List<String> childNames(PbcsMember member) {
        return member.getChildren().stream().map(PbcsMember::getName).collect(toList());
    }

}
