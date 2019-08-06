package com.jasonwjones.pbcs.api.v3.dataslices;

import java.util.List;

public class ArrayUtils {
	
	public static void printLists(List<List<String>> data) {
		
		for (List<String> list : data) {
			for (String cell : list) {
				System.out.printf("[%20s]", cell);
			}
			System.out.println();
		}
		
	}

}
