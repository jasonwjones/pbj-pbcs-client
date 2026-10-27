package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsApplicationSubstitutionVariable;
import com.jasonwjones.pbcs.client.PbcsObject;
import com.jasonwjones.pbcs.client.PbcsPlanningClient;
import com.jasonwjones.pbcs.client.PbcsSubstitutionVariableScope;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Default {@link PbcsApplicationSubstitutionVariable} implementation, for a variable defined for a whole application.
 */
public class PbcsApplicationSubstitutionVariableImpl implements PbcsApplicationSubstitutionVariable {

	private final PbcsApplication application;

	private final String name;

	private final String value;

	/**
	 * Constructs an application-scoped variable.
	 *
	 * @param application the application the variable is defined for
	 * @param name the variable name
	 * @param value the variable value
	 */
	public PbcsApplicationSubstitutionVariableImpl(PbcsApplication application, String name, String value) {
		this.application = Objects.requireNonNull(application, "application");
		this.name = name;
		this.value = value;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public PbcsSubstitutionVariableScope getScope() {
		return PbcsSubstitutionVariableScope.APPLICATION;
	}

	@Override
	public PbcsApplication getApplication() {
		return application;
	}

	@Override
	public PbcsPlanningClient getClient() {
		return application.getClient();
	}

	@Override
	public PbcsObject getParent() {
		return application;
	}

	@Override
	public PbcsApplicationSubstitutionVariableImpl withValue(String value) {
		return new PbcsApplicationSubstitutionVariableImpl(application, name, value);
	}

	/**
	 * Identifies the variable for {@link #equals(Object)} and {@link #hashCode()}: its scope, where it's defined,
	 * name and value.
	 *
	 * @return the identifying values
	 */
	protected List<Object> identity() {
		return Arrays.asList(getScope(), application.getName(), name, value);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		return identity().equals(((PbcsApplicationSubstitutionVariableImpl) o).identity());
	}

	@Override
	public int hashCode() {
		return identity().hashCode();
	}

	@Override
	public String toString() {
		return getQualifiedName() + "=" + value;
	}

}
