package com.jasonwjones.pbcs.cli.command;

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
public class ListVariableCommand extends AbstractApplicationCommand {
	
	@Parameter(names = "--variable", required = true, description = "List value for particular variable")
	private String variableName;
		
	@Override
	public void executeForApp(PbcsApplication application) {
		SubstitutionVariable variable = application.getSubstitutionVariable(variableName);
		System.out.println(variable.getValue());
	}
	
}
