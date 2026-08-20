package com.jasonwjones.pbcs.client;

import java.util.HashMap;
import java.util.Map;

/**
 * The numeric job status codes returned by the PBCS REST API for a launched job.
 */
public enum PbcsJobStatusCode {

	/**
	 * The job is still running.
	 */
	IN_PROGRESS(-1, "In Progress"),

	/**
	 * The job completed successfully.
	 */
	SUCCESS(0, "Success"),

	/**
	 * The job failed with an error.
	 */
	ERROR(1, "Error"),

	/**
	 * A cancellation of the job has been requested but not yet completed.
	 */
	CANCEL_PENDING(2, "Cancel Pending"),

	/**
	 * The job was cancelled.
	 */
	CANCELLED(3, "Cancelled"),

	/**
	 * The job was launched with an invalid parameter.
	 */
	INVALID_PARAMETER(4, "Invalid Parameter"),

	/**
	 * A status code not otherwise recognized by this library.
	 */
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

	/**
	 * Looks up the enum value for the given numeric status code.
	 *
	 * @param code the numeric status code
	 * @return the matching enum value, or {@link #UNKNOWN} if not recognized
	 */
	public static PbcsJobStatusCode valueOf(int code) {
		return lookups.getOrDefault(code, PbcsJobStatusCode.UNKNOWN);
	}

	PbcsJobStatusCode(int code, String description) {
		this.code = code;
		this.description = description;
	}

	/**
	 * Gets the numeric status code.
	 *
	 * @return the status code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * Gets a human-readable description of this status.
	 *
	 * @return the description
	 */
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

	/**
	 * Returns true if the job was successful, i.e. is equal to {@link #SUCCESS}.
	 *
	 * @return true if successful, false otherwise
	 */
	public boolean isSuccessful() {
		return this == SUCCESS;
	}

}