package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsObject;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.client.PbcsPlanTypeSubstitutionVariable;
import com.jasonwjones.pbcs.client.PbcsSubstitutionVariableScope;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Default {@link PbcsPlanTypeSubstitutionVariable} implementation, for a variable defined for one plan type.
 * <p>
 * A variable read from the application's list knows only its plan type's name, and looks the plan type up on first
 * use; one read from the plan type, or made from one, already has it.
 */
public class PbcsPlanTypeSubstitutionVariableImpl extends PbcsApplicationSubstitutionVariableImpl implements PbcsPlanTypeSubstitutionVariable {

	private final String planTypeName;

	private volatile PbcsPlanType planType;

	/**
	 * Constructs a variable for a plan type that's already at hand.
	 *
	 * @param planType the plan type the variable is defined for
	 * @param name the variable name
	 * @param value the variable value
	 */
	public PbcsPlanTypeSubstitutionVariableImpl(PbcsPlanType planType, String name, String value) {
		this(planType.getApplication(), planType.getName(), planType, name, value);
	}

	/**
	 * Constructs a variable for a plan type known only by name, which {@link #getPlanType()} looks up when first
	 * asked.
	 *
	 * @param application the application the plan type belongs to
	 * @param planTypeName the name of the plan type the variable is defined for
	 * @param name the variable name
	 * @param value the variable value
	 */
	public PbcsPlanTypeSubstitutionVariableImpl(PbcsApplication application, String planTypeName, String name, String value) {
		this(application, planTypeName, null, name, value);
	}

	private PbcsPlanTypeSubstitutionVariableImpl(PbcsApplication application, String planTypeName, PbcsPlanType planType, String name, String value) {
		super(application, name, value);
		this.planTypeName = Objects.requireNonNull(planTypeName, "planTypeName");
		this.planType = planType;
	}

	@Override
	public PbcsSubstitutionVariableScope getScope() {
		return PbcsSubstitutionVariableScope.PLAN_TYPE;
	}

	@Override
	public String getPlanTypeName() {
		return planTypeName;
	}

	@Override
	public PbcsPlanType getPlanType() {
		PbcsPlanType resolved = planType;
		if (resolved == null) {
			resolved = getApplication().getPlanType(planTypeName);
			planType = resolved;
		}
		return resolved;
	}

	@Override
	public PbcsObject getParent() {
		return getPlanType();
	}

	/**
	 * Builds the qualified name from the plan type's name, so that naming the variable never looks the plan type up.
	 *
	 * @return the qualified name, such as <code>Vision.Plan1.Plan1Yr</code>
	 */
	@Override
	public String getQualifiedName() {
		return getApplication().getQualifiedName() + "." + planTypeName + "." + getName();
	}

	@Override
	public PbcsPlanTypeSubstitutionVariableImpl withValue(String value) {
		return new PbcsPlanTypeSubstitutionVariableImpl(getApplication(), planTypeName, planType, getName(), value);
	}

	@Override
	protected List<Object> identity() {
		return Arrays.asList(getScope(), getApplication().getName(), planTypeName, getName(), getValue());
	}

}
