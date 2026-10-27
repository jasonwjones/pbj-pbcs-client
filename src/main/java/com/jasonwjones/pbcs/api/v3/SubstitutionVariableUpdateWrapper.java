package com.jasonwjones.pbcs.api.v3;

/**
 * A concrete {@link AbstractHypermediaPayload} of {@link SubstitutionVariable} items, used as the request
 * payload for updating substitution variables.
 */
@SuppressWarnings("deprecation") // the payload class is deprecated as public API, not as a payload
public class SubstitutionVariableUpdateWrapper extends AbstractHypermediaPayload<SubstitutionVariable> {

	/**
	 * Constructs an empty instance.
	 */
	public SubstitutionVariableUpdateWrapper() {
	}

}
