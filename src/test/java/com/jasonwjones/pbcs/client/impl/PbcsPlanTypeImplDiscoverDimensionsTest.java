package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.api.v3.PlanTypeDimension;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsDimension;
import com.jasonwjones.pbcs.client.PbcsMemberType;
import com.jasonwjones.pbcs.client.exceptions.PbcsInvalidDimensionException;
import org.junit.Test;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;

public class PbcsPlanTypeImplDiscoverDimensionsTest {

    private final RestContext context = new RestContext(null, null, null, null);

    @Test
    public void classifiesAttributeDimensionsAutomatically() {
        PbcsPlanTypeImpl planType = planType();

        List<PbcsDimension> dimensions = planType.buildDiscoveredDimensions(
                Arrays.asList(dimension("Account", "Account", true), dimension("Market", "Attribute Dimension", true)),
                false);

        assertThat(dimensions.get(0).getDimensionType(), is(PbcsMemberType.ACCOUNT));
        assertThat(dimensions.get(1).getDimensionType(), is(PbcsMemberType.ATTRIBUTE));
    }

    @Test
    public void preservesOrderAndAssignsSequentialNumbers() {
        PbcsPlanTypeImpl planType = planType();

        List<PbcsDimension> dimensions = planType.buildDiscoveredDimensions(
                Arrays.asList(dimension("Account", "Account", true), dimension("Period", "Period", true), dimension("Entity", "Entity", true)),
                false);

        assertThat(dimensions.stream().map(PbcsDimension::getName).toList(), contains("Account", "Period", "Entity"));
        assertThat(dimensions.get(0).getNumber(), is(0));
        assertThat(dimensions.get(1).getNumber(), is(1));
        assertThat(dimensions.get(2).getNumber(), is(2));
    }

    @Test
    public void throwsOnFirstInvalidDimensionWhenValidatingIsEnabled() {
        PbcsPlanTypeImpl planType = planType();
        List<PlanTypeDimension> dimensions = Arrays.asList(
                dimension("Account", "Account", true),
                dimension("BadDimension", "Custom", false),
                dimension("Period", "Period", false));

        PbcsInvalidDimensionException exception = assertThrows(PbcsInvalidDimensionException.class,
                () -> planType.buildDiscoveredDimensions(dimensions, true));

        assertThat(exception.getObjectName(), is("BadDimension"));
    }

    @Test
    public void doesNotThrowWhenValidateDimensionsDisabledEvenIfInvalid() {
        PbcsPlanTypeImpl planType = planType();
        List<PlanTypeDimension> dimensions = Arrays.asList(
                dimension("Account", "Account", true),
                dimension("BadDimension", "Custom", false));

        List<PbcsDimension> result = planType.buildDiscoveredDimensions(dimensions, false);

        assertThat(result.stream().map(PbcsDimension::getName).toList(), contains("Account", "BadDimension"));
    }

    private PbcsPlanTypeImpl planType() {
        return new PbcsPlanTypeImpl(context, application(), new PlanTypeConfigurationImpl.Builder("Plan1").build());
    }

    private PbcsApplication application() {
        return (PbcsApplication) Proxy.newProxyInstance(
                PbcsApplication.class.getClassLoader(),
                new Class<?>[] {PbcsApplication.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "getName" -> "Vision";
                    case "getParent" -> null;
                    default -> throw new UnsupportedOperationException(method.getName());
                });
    }

    private static PlanTypeDimension dimension(String name, String dimType, boolean valid) {
        PlanTypeDimension dimension = new PlanTypeDimension();
        dimension.setDimensionName(name);
        dimension.setDimType(dimType);
        dimension.setValid(valid);
        return dimension;
    }

}
