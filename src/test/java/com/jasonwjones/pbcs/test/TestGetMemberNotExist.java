package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsMemberProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestGetMemberNotExist extends AbstractIntegrationTest {

	private static final Logger logger = LoggerFactory.getLogger(TestGetMemberNotExist.class);

	public static void main(String[] args) {
		PbcsClient client = new PbcsClientFactory().createClient(connection);

		PbcsMemberProperties member = client.getApplication("Vision").getMember("Account", "4110");
		printMember(member, 0);

		PbcsMemberProperties member2 = client.getApplication("Vision").getMember("Account", "4110X");
		printMember(member2, 0);
	}

	private static void printMember(PbcsMemberProperties member, int level) {
		System.out.print(repeat("    ", level));
		System.out.printf("%s (%s) %n", member.getName(), member.getDataStorage());

		for (PbcsMemberProperties child : member.getChildren()) {
			printMember(child, level + 1);
		}
	}

}