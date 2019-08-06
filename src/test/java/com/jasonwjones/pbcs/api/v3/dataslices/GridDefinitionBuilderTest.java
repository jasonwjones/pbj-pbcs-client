package com.jasonwjones.pbcs.api.v3.dataslices;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class GridDefinitionBuilderTest {

	private static List<String> dims = Arrays.asList("Market", "Period", "Scenario", "Product", "Accounts");
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		GridDefinition grid = new GridDefinitionBuilder().auto(dims).build();
		
		
		
		fail("Not yet implemented");
	}

}
