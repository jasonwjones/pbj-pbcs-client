package com.jasonwjones.pbcs.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsConnection;
import com.jasonwjones.pbcs.client.PbcsMemberProperties;
import com.jasonwjones.pbcs.client.impl.PbcsConnectionImpl;

public class TestGetMemberSso extends AbstractIntegrationTest {

	private static final Logger logger = LoggerFactory.getLogger(TestGetMemberSso.class);
	
	public static void main(String[] args) {
		PbcsConnection conn = new PbcsConnectionImpl("vecghypdemo-vecghypdemo.pbcs.us2.oraclecloud.com", "vecghypdemo", "jason@appliedolap.com", "Ja$on100");
		PbcsClient client = new PbcsClientFactory().createClient(conn);
		//PbcsClient client = new PbcsClientFactory().createClient(connection);
		
		logger.info("Applications: {}", client.getApplications());
		
//		PbcsMemberProperties member = client
//				.getApplication(appName) 
//				.getMember("Period", "Q1");
//		
//		printMember(member, 0);		
	}
	
	
	private static void printMember(PbcsMemberProperties member, int level) {
		System.out.print(repeat("    ", level));
		System.out.println(member.getName());

		for (PbcsMemberProperties child : member.getChildren()) {
			printMember(child, level + 1);
		}
	}
	
}
