package com.jasonwjones.pbcs.test;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.util.Outline;
import com.jasonwjones.pbcs.util.Outline.Member;

public class TestOutline extends AbstractIntegrationTest {

	private static final Logger logger = LoggerFactory.getLogger(TestOutline.class);
	
	public static void main(String[] args) {
		PbcsClient client = new PbcsClientFactory().createClient(connection);
		
		logger.info("Applications: {}", client.getApplications());
		
		PbcsApplication app = client.getApplication(appName);
		
		Outline outline = new Outline(app);
		
		List<Member> children = outline.executeQuery(null, "YearTotal");
		
		for (Member member : children) {
			logger.info("Child: {} at gen {}, lev {}", member.getName(), member.getGeneration(), member.getLevel());
		}
		
		//outline.e
		
//		logger.info("Gen of Period: {}", outline.getGeneration("Period"));
//		logger.info("Gen of YearTotal: {}", outline.getGeneration("YearTotal"));
//		logger.info("Gen of Q1: {}", outline.getGeneration("Q1"));
		
	}	
	
}
