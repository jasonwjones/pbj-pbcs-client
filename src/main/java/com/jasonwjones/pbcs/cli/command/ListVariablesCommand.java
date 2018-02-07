package com.jasonwjones.pbcs.cli.command;

import java.util.Set;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.jasonwjones.pbcs.api.v3.SubstitutionVariable;
import com.jasonwjones.pbcs.client.PbcsApplication;

/**
 * List available applications
 * 
 * @author jasonwjones
 *
 */
@Parameters(separators = "=", commandDescription = "List substitution variables")
public class ListVariablesCommand extends AbstractApplicationCommand {
	
	private static final String DEFAULT_FORMAT = "%s,%s,%s%n";
	
	@Parameter(names = "--format", required = false, description = "Provide custom format string, default is: " + DEFAULT_FORMAT)
	private String formatTemplate = DEFAULT_FORMAT;
	
	@Override
	public void executeForApp(PbcsApplication application) {
		Set<SubstitutionVariable> variables = application.getSubstitutionVariables();
		for (SubstitutionVariable variable : variables) {
			System.out.printf(formatTemplate, variable.getPlanType(), variable.getName(), variable.getValue());
		}
	}

}
