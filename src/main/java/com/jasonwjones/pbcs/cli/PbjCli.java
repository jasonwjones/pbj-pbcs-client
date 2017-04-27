package com.jasonwjones.pbcs.cli;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.cli.command.EncodePasswordCommand;
import com.jasonwjones.pbcs.cli.command.GetMemberCommand;
import com.jasonwjones.pbcs.cli.command.ListApplicationsCommand;
import com.jasonwjones.pbcs.cli.command.MainCommand;
import com.jasonwjones.pbcs.cli.command.PbjCliCommand;
import com.jasonwjones.pbcs.client.impl.PbcsConnectionImpl;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

/**
 * Provides a simple CLI for interacting with the PBCS REST API
 * 
 * @author jasonwjones
 *
 */
public class PbjCli {

	public static void main(String[] args) {
		
		MainCommand main = new MainCommand();
		JCommander jc = new JCommander(main);
		
		Map<String, PbjCliCommand> commands = new HashMap<String, PbjCliCommand>();
		commands.put("get-member", new GetMemberCommand());
		commands.put("encode-password", new EncodePasswordCommand());
		commands.put("list-applications", new ListApplicationsCommand());

		for (Map.Entry<String, ?> command : commands.entrySet()) {
			jc.addCommand(command.getKey(), command.getValue());
		}
		
		try {
			jc.parse(args);
			
			if (!main.isDebugLogging()) {
				Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
				root.setLevel(Level.DEBUG);
			} else {
				Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
				root.setLevel(Level.DEBUG);				
			}
			
			if (main.isLoggingOff()) {
				Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
				root.setLevel(Level.OFF);				
			}
			
			String command = jc.getParsedCommand();
			if (command != null && !command.isEmpty()) {
				if (commands.containsKey(command)) {
					PbjCliCommand commandToExec = commands.get(command);
					if (commandToExec.requiresConnection()) {
						Properties conn = main.getConfigFile();
						if (conn != null) {
							commandToExec.execute(new PbcsClientFactory().createClient(PbcsConnectionImpl.fromProperties(conn)));
						} else {
							System.err.println("Command requires a connection but properties file not specified or not found");
						}
					} else {
						commandToExec.execute(null);
					}
				}
			} else {
				throw new ParameterException("No command was specified");
			}
		} catch (ParameterException e) {
			jc.usage();
			System.exit(1);
		}
	}
	
}
