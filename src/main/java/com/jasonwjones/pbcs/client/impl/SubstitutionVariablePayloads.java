package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.api.v3.SubstitutionVariable;
import com.jasonwjones.pbcs.api.v3.SubstitutionVariableUpdateWrapper;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsApplicationSubstitutionVariable;
import com.jasonwjones.pbcs.client.PbcsPlanTypeSubstitutionVariable;
import com.jasonwjones.pbcs.client.PbcsSubstitutionVariable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Converts between the REST API's substitution variable payload and the public {@link PbcsSubstitutionVariable}
 * types, so the payload stays out of the public API.
 */
@SuppressWarnings("deprecation") // the payload class is deprecated only as public API; this is its intended use
final class SubstitutionVariablePayloads {

	private SubstitutionVariablePayloads() {}

	/**
	 * Wraps a variable read from the application's list, at the scope its plan type says: <code>ALL</code> (or none)
	 * for the application, otherwise the named plan type.
	 *
	 * @param application the application the variable was read from
	 * @param payload the variable as the REST API returned it
	 * @return the variable
	 */
	static PbcsApplicationSubstitutionVariable fromPayload(PbcsApplication application, SubstitutionVariable payload) {
		if (isApplicationScoped(payload)) {
			return new PbcsApplicationSubstitutionVariableImpl(application, payload.getName(), payload.getValue());
		}
		return new PbcsPlanTypeSubstitutionVariableImpl(application, payload.getPlanType(), payload.getName(), payload.getValue());
	}

	/**
	 * Says whether a variable read from the REST API is defined for the whole application.
	 *
	 * @param payload the variable as the REST API returned it
	 * @return true if its plan type is <code>ALL</code> or absent
	 */
	private static boolean isApplicationScoped(SubstitutionVariable payload) {
		return payload.getPlanType() == null || SubstitutionVariable.DEFAULT_PLANTYPE.equals(payload.getPlanType());
	}

	/**
	 * Builds the body of an update request, which sets variables of either scope in one call.
	 *
	 * @param variables the variables to set
	 * @return the request body
	 */
	static SubstitutionVariableUpdateWrapper updateRequest(Collection<? extends PbcsSubstitutionVariable> variables) {
		List<SubstitutionVariable> items = new ArrayList<>();
		for (PbcsSubstitutionVariable variable : variables) {
			items.add(toPayload(variable));
		}
		SubstitutionVariableUpdateWrapper request = new SubstitutionVariableUpdateWrapper();
		request.setItems(items);
		return request;
	}

	private static SubstitutionVariable toPayload(PbcsSubstitutionVariable variable) {
		SubstitutionVariable payload = new SubstitutionVariable(variable.getName(), variable.getValue());
		switch (variable.getScope()) {
		case APPLICATION:
			payload.setPlanType(SubstitutionVariable.DEFAULT_PLANTYPE);
			break;
		case PLAN_TYPE:
			payload.setPlanType(((PbcsPlanTypeSubstitutionVariable) variable).getPlanTypeName());
			break;
		default:
			throw new IllegalArgumentException("Can't update a " + variable.getScope() + " variable: " + variable.getName());
		}
		return payload;
	}

}
