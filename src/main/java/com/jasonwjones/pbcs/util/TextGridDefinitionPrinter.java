package com.jasonwjones.pbcs.util;

import java.util.List;

import com.jasonwjones.pbcs.api.v3.dataslices.DimensionMembers;
import com.jasonwjones.pbcs.api.v3.dataslices.GridDefinition;

public class TextGridDefinitionPrinter {

	public static void print(GridDefinition gridDefinition) {
		System.out.println("POV:");
		printDimensionMembers(gridDefinition.getPov());
		
		System.out.println("Rows:");
		for (DimensionMembers dm : gridDefinition.getRows()) {
			printDimensionMembers(dm);
		}
		
		System.out.println("Cols:" );
		for (DimensionMembers dm : gridDefinition.getColumns()) {
			printDimensionMembers(dm);
		}
	}
	
	private static void printDimensionMembers(DimensionMembers dimensionMembers) {
		System.out.println("  Dims: " + dimensionMembers.getDimensions());
		for (List<String> list : dimensionMembers.getMembers()) {
			System.out.print("  - ");
			for (String member : list) {
				System.out.print(member + ", ");
			}
			System.out.println();
		}
	}
	
}
