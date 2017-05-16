package com.jasonwjones.pbcs.testcli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractCliTest {

	private static final Logger logger = LoggerFactory.getLogger(AbstractCliTest.class);
	
	private static final String PROPS = System.getProperty("user.home") +"/pbcs-client.properties";
		
	public static String[] allArgs(String... currentArgs) {
		return combineString(new String[]{"--conn-properties", PROPS}, currentArgs);
	}
	
	public static String[] combineString(String[] first, String[] second){
        int length = first.length + second.length;
        String[] result = new String[length];
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
	
	public static String concat(String[] items) {
		StringBuilder sb = new StringBuilder();
		for (String item : items) {
			sb.append(item);
			sb.append(" ");
		}
		return sb.toString();
	}
	
}
