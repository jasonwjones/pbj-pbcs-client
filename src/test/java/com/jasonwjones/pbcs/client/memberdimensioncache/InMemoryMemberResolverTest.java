package com.jasonwjones.pbcs.client.memberdimensioncache;

import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsMember;
import com.jasonwjones.pbcs.client.PbcsPlanType;

import org.junit.Test;

import java.lang.reflect.Proxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class InMemoryMemberResolverTest {

	private final InMemoryMemberResolver resolver = new InMemoryMemberResolver();

	private final PbcsPlanType plan = plan("Vision", "Plan1");

	@Test
	public void remembersAMemberByTheNameThatResolvedIt() {
		PbcsMember member = someMember();
		assertNull(resolver.getMember(plan, "Q1"));

		resolver.setMember(plan, "Q1", member);

		assertEquals(member, resolver.getMember(plan, "Q1"));
	}

	/** Nothing resolved is still nothing to remember - a null must not be cached as an answer. */
	@Test
	public void doesNotRememberAFailureToResolve() {
		resolver.setMember(plan, "NoSuchMember", null);
		resolver.setDimension(plan, "NoSuchMember", null);
		resolver.setAlias(plan, "NoSuchMember", "Default", null);

		assertNull(resolver.getMember(plan, "NoSuchMember"));
		assertNull(resolver.getDimensionName(plan, "NoSuchMember"));
		assertNull(resolver.getAlias(plan, "NoSuchMember", "Default"));
	}

	/** Aliases differ per table, so two tables are two answers for the same member. */
	@Test
	public void keepsAliasesApartByTable() {
		resolver.setAlias(plan, "410", "Default", "Sales");
		resolver.setAlias(plan, "410", "German", "Umsatz");

		assertEquals("Sales", resolver.getAlias(plan, "410", "Default"));
		assertEquals("Umsatz", resolver.getAlias(plan, "410", "German"));
	}

	/** Null, blank and "Default" are one table as far as the REST API is concerned. */
	@Test
	public void treatsTheUnnamedTableAsDefault() {
		resolver.setAlias(plan, "410", null, "Sales");

		assertEquals("Sales", resolver.getAlias(plan, "410", "Default"));
		assertEquals("Sales", resolver.getAlias(plan, "410", ""));
		assertEquals("Sales", resolver.getAlias(plan, "410", "default"));
	}

	/**
	 * One resolver can serve more than one plan - the interface passes the plan to every call for
	 * exactly that reason - so a member's answer must not leak from one cube to another.
	 */
	@Test
	public void keepsPlansApart() {
		PbcsPlanType other = plan("Vision", "Vision");
		resolver.setDimension(plan, "Q1", "Period");
		resolver.setAlias(plan, "Q1", "Default", "Quarter 1");

		assertNull(resolver.getDimensionName(other, "Q1"));
		assertNull(resolver.getAlias(other, "Q1", "Default"));
		assertEquals("Period", resolver.getDimensionName(plan, "Q1"));
	}

	@Test
	public void keepsApplicationsApart() {
		PbcsPlanType elsewhere = plan("Other", "Plan1");
		resolver.setDimension(plan, "Q1", "Period");

		assertNull(resolver.getDimensionName(elsewhere, "Q1"));
	}

	/**
	 * A plan type that answers only the two questions the resolver asks of one - its name and its
	 * application's. A proxy rather than a hand-written stub because the interface is large and this
	 * needs none of the rest of it, and rather than a mocking library because the project has none.
	 */
	private static PbcsPlanType plan(String applicationName, String planName) {
		PbcsApplication application = (PbcsApplication) Proxy.newProxyInstance(
				PbcsApplication.class.getClassLoader(), new Class<?>[] {PbcsApplication.class},
				(proxy, method, args) -> answer(method.getName(), "getName", applicationName));
		return (PbcsPlanType) Proxy.newProxyInstance(
				PbcsPlanType.class.getClassLoader(), new Class<?>[] {PbcsPlanType.class},
				(proxy, method, args) -> {
					if ("getApplication".equals(method.getName())) {
						return application;
					}
					return answer(method.getName(), "getName", planName);
				});
	}

	/**
	 * A member that is nothing but itself: the resolver stores and returns it without asking it
	 * anything, so all it has to do is compare equal to itself, which is what the test asserts.
	 */
	private static PbcsMember someMember() {
		return (PbcsMember) Proxy.newProxyInstance(
				PbcsMember.class.getClassLoader(), new Class<?>[] {PbcsMember.class},
				(proxy, method, args) -> switch (method.getName()) {
					case "equals" -> proxy == args[0];
					case "hashCode" -> System.identityHashCode(proxy);
					case "toString" -> "a member";
					default -> throw new UnsupportedOperationException(
							"the resolver should not be calling " + method.getName());
				});
	}

	private static Object answer(String called, String expected, String value) {
		return switch (called) {
			case "hashCode" -> value.hashCode();
			case "toString" -> value;
			default -> {
				if (expected.equals(called)) {
					yield value;
				}
				throw new UnsupportedOperationException("the resolver should not be calling " + called);
			}
		};
	}
}
