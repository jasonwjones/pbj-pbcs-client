package com.jasonwjones.pbcs.testcli;

import com.jasonwjones.pbcs.cli.PbjCli;

public class VariablesViewFormatterTest extends AbstractCliTest {
	
	public static void main(String[] args) {
		String[] all = allArgs("list-variables", "--application=MDP_Demo", "--format=%s|%s|%s%n");
		PbjCli.main(all);
	}

}
