package com.jasonwjones.pbcs.client.exceptions;

/**
 * Future use: thrown when there's a problem adding a member to a cube
 * 
 * @author jasonwjones
 *
 */
@SuppressWarnings("serial")
public class PbcsMemberAddException extends PbcsClientException {

	private String instance;

	private String type;

	private String detail;

	private Integer status;

	private String errorPath;

	private String title;

	// might really be an int
	private String errorCode;

	private String errorDetails;

	private String message;

	private String localizedMessage;

	public PbcsMemberAddException() {
		super("Error adding member");
	}

}
