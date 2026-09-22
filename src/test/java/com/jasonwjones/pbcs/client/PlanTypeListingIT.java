package com.jasonwjones.pbcs.client;

import com.jasonwjones.pbcs.util.PbcsClientUtils;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * That listing an application's plan types lists all of its cubes.
 *
 * <p>It did not, for as long as the list came from a Data Management application record: those carry
 * six named slots for block storage plan types, so an aggregate storage reporting cube was absent from
 * every listing, and so was any seventh plan type. Planning answers this itself now.
 */
public class PlanTypeListingIT {

	private PbcsApplication application;

	@Before
	public void setUp() {
		Properties properties = PbcsClientUtils.connectionProperties();
		application = PbcsClientUtils.client().getApplication(properties.getProperty("appName"));
	}

	@Test
	public void listsEveryCubeIncludingAggregateStorageOnes() {
		List<String> names = new ArrayList<>();
		for (PbcsPlanType planType : application.getPlanTypes()) {
			names.add(planType.getName());
		}
		System.out.println("plan types: " + names);

		assertFalse("no plan types came back at all", names.isEmpty());
		// Vision's reporting cube shares the application's name and is the one the old listing dropped.
		assertTrue("expected the reporting cube in " + names, names.contains(application.getName()));
	}

	/** And each listed name really is a plan type, so validation accepts it. */
	@Test
	public void everyListedPlanTypeCanBeOpened() {
		for (PbcsPlanType listed : application.getPlanTypes()) {
			PbcsPlanType opened = application.getPlanType(listed.getName());
			assertTrue("opened plan " + opened.getName() + " should know its dimensions",
					!opened.getDimensions().isEmpty());
		}
	}
}
