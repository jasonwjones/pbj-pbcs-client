package com.jasonwjones.pbcs.client;

import java.util.HashMap;
import java.util.Map;

public enum PbcsJobStatusCode {

	IN_PROGRESS(-1, "In Progress"),

	SUCCESS(0, "Success"),

	ERROR(1, "Error"),

	CANCEL_PENDING(2, "Cancel Pending"),

	CANCELLED(3, "Cancelled"),

	INVALID_PARAMETER(4, "Invalid Parameter"),

	UNKNOWN(Integer.MAX_VALUE, "Unknown");

	private final int code;

	private final String description;

	private final static Map<Integer, PbcsJobStatusCode> lookups;

	static {
		lookups = new HashMap<>();
		for (PbcsJobStatusCode jobStatusCode : PbcsJobStatusCode.values()) {
			lookups.put(jobStatusCode.getCode(), jobStatusCode);
		}
	}

	public static PbcsJobStatusCode valueOf(int code) {
		return lookups.getOrDefault(code, PbcsJobStatusCode.UNKNOWN);
	}

	PbcsJobStatusCode(int code, String description) {
		this.code = code;
		this.description = description;
	}

	public int getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * Convenience method to determine if the job is still running. This method
	 * returns true if the status of the job is neither -1 (In Progress) and 2
	 * (Cancel Pending).
	 *
	 * @return true if the job is done running, false otherwise
	 */
	public boolean isFinished() {
		return code != -1 && code != 2;
	}

}