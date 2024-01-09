package com.jasonwjones.pbcs.test;

import java.util.Arrays;
import java.util.List;

import com.jasonwjones.pbcs.client.PbcsMemberProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsDimension;
import com.jasonwjones.pbcs.client.PbcsPlanType;

public class TestGetApplication extends AbstractIntegrationTest {

	private static final Logger logger = LoggerFactory.getLogger(TestGetApplication.class);

	public static void main(String[] args) {
		PbcsClient client = new PbcsClientFactory().createClient(connection);

		System.out.println("Apps:");
		//logger.info("Applications: {}", client.getApplications());

		for (PbcsApplication app : client.getApplications()) {
			logger.info("App: {}", app);
			List<PbcsPlanType> planTypes = app.getPlanTypes();
			System.out.println("HAVE " + planTypes.size() + " plan types");
			for (PbcsPlanType planType : planTypes) {
				System.out.println(" - plan: " + planType.getName());

			}

			for (PbcsDimension dimension : app.getDimensions()) {
				System.out.println(" - " + dimension.getName());
			}
		}

//		List<PbcsDimension> dims = client.getApplication("Vision").getPlanType("Plan1").getDimensions();
//		System.out.println("Have " + dims.size() + " dimensions");
		String cell = client.getApplication("Vision").getPlanType("Plan1").getCell(Arrays.asList("4110", "USD", "000", "Jan", "P_000", "Actual", "Final", "FY22"));

		PbcsMemberProperties actual = client.getApplication("Vision").getPlanType("Plan1").getMember("Scenario", "Actual");
		System.out.println("Member: " + actual.getName());

		PbcsMemberProperties current = client.getApplication("Vision").getPlanType("Plan1").getMember("Scenario", "Current");
		System.out.println("Member: " + current.getName());
		for (PbcsMemberProperties child : current.getChildren()) {
			System.out.println("Child: " + child.getName());
		}

		System.out.println("Cell: " + cell);


	}

}