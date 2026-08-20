package com.jasonwjones.pbcs.client.exceptions;

/**
 * Future use: thrown when there's a problem adding a member to a cube
 * 
 * @author jasonwjones
 *
 */
@SuppressWarnings("serial")
public class PbcsMemberAddException extends PbcsClientException {

	/**
	 * The instance value from the error payload, if any.
	 */
	private String instance;

	/**
	 * The type value from the error payload, if any.
	 */
	private String type;

	/**
	 * The detail value from the error payload, if any.
	 */
	private String detail;

	/**
	 * The numeric status code from the error payload, if any.
	 */
	private Integer status;

	/**
	 * The error path from the error payload, if any.
	 */
	private String errorPath;

	/**
	 * The title value from the error payload, if any.
	 */
	private String title;

	// might really be an int
	/**
	 * The error code from the error payload, if any.
	 */
	private String errorCode;

	/**
	 * The error details from the error payload, if any.
	 */
	private String errorDetails;

	/**
	 * The message from the error payload, if any.
	 */
	private String message;

	/**
	 * The localized message from the error payload, if any.
	 */
	private String localizedMessage;

	/**
	 * Constructs an instance with a default message.
	 */
	public PbcsMemberAddException() {
		super("Error adding member");
	}

}
