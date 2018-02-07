package com.jasonwjones.pbcs.testcli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jasonwjones.pbcs.cli.PbjCli;

public class VariablesViewFormatterTest extends AbstractCliTest {
	
	private static final Logger logger = LoggerFactory.getLogger(VariablesViewFormatterTest.class);
	
	public static void main(String[] args) {
		String[] all = allArgs("list-variables", "--application=MDP_Demo", "--format=%s|%s|%s%n");
		logger.info("Parameters: {}", concat(all));
		PbjCli.main(all);
	}

}
