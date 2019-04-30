package com.jasonwjones.pbcs.client.exceptions;

public class PbcsGeneralException extends PbcsClientException {

	private PbcsErrorResponse errorResponse;
	
	//Body: {"detail":"The dimension PeriodX is invalid.","status":400,"message":"com.hyperion.planning.InvalidDimensionException: The dimension PeriodX is invalid.","localizedMessage":"com.hyperion.planning.InvalidDimensionException: The dimension PeriodX is invalid."}
	public PbcsGeneralException(PbcsErrorResponse errorResponse) {
		super(errorResponse.getDetail());
		this.errorResponse = errorResponse;
	}
	
}
