package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsJobType;
import com.jasonwjones.pbcs.utils.PbcsClientUtils;

public class TestGetRules {

	public static void main(String[] args) {
		PbcsApplication app = PbcsClientUtils.vision();
		app.getJobDefinitions(PbcsJobType.RULES);
		app.launchBusinessRule("AggAll");
	}

}