package com.jasonwjones.pbcs.client.exceptions;

/**
 * Thrown when a requested file does not exist on the remote system.
 */
@SuppressWarnings("serial")
public class PbcsNoSuchFileException extends PbcsClientException {

	/**
	 * Constructs an instance for the given filename.
	 *
	 * @param filename the name of the file that does not exist
	 */
	public PbcsNoSuchFileException(String filename) {
		super(filename);
	}

}
