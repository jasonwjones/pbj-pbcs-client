package com.jasonwjones.pbcs.cli.command;

import com.jasonwjones.pbcs.PbcsClient;

/**
 * Simple model for commands available in the CLI
 * 
 * @author jasonwjones
 *
 */
public interface PbjCliCommand {

	/**
	 * Whether this command requires a PBCS client connection or not. If true,
	 * then the first parameter will be an established client connection,
	 * otherwise null.
	 * 
	 * @return false if this command does not need a connection (such as the
	 *         password encode function).
	 */
	public boolean requiresConnection();

	/**
	 * Run this commmand. If the command returns true for
	 * {@link #requiresConnection()} then this will be passed in an established
	 * client, otherwise null.
	 * 
	 * @param client the client object to use for operations
	 */
	public void execute(PbcsClient client);

}
