package com.jasonwjones.pbcs.api.v3;

/**
 * Maps the user preferences payload returned by the REST API's user preferences endpoint. Currently unmapped
 * beyond its fields; reserved for future use.
 */
public class PbcsUserPreferencesModel {

	private String thousandsSeparator;

	private Integer negativeStyle;

	private String currSymbol;

	private boolean showPUAlias;

	private Integer minPrecision;

	private Integer maxPrecision;

	private String decimalSeparator;

	private Integer scale;

	// TODO: payload has links object in it
	// docs indicate that "type" (type of app) is in payload but it doesn't seem to actually be in

	/**
	 * Constructs an empty instance for deserialization.
	 */
	public PbcsUserPreferencesModel() {
	}

}
