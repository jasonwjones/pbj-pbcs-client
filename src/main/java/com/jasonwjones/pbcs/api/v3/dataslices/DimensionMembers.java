package com.jasonwjones.pbcs.api.v3.dataslices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Models a single instance of the cartesian inputs for a part of an OLAP axis.
 * For example, if the "left" axis of a given report has the dimensions Time and
 * Years, and it was desired to have Jan and Feb for FY18 and Mar for FY19, then
 * two DimensionMembers would be used, with the following configuration:
 * 
 * <pre>
 * DimensionMembers 1 = Jan, Feb | FY18 
 * DimensionMembers 2 = Mar | FY19
 * </pre>
 * 
 * This would yield three full rows.
 * 
 * @author jasonwjones
 *
 */
public class DimensionMembers {

	/**
	 * At present, the PBCS REST API requires that the dimension names are
	 * specified in the POV, but they can be blank for row/column
	 * specifications. Therefore, this needs to be set when it's used in the
	 * POV, but can be left null (not an empty collection) in one of the other
	 * orientations. Do note however that Oracle recommends specifying the
	 * dimension names for performance reasons.F
	 */
	private List<String> dimensions;

	/**
	 * The list of list data structure can be visualized in the following way
	 * (for example):
	 * 
	 * <pre>
	 * [Jan                 ][Feb                 ][Mar                 ]
	 * [Actual              ][Actual              ][Actual              ]
	 * </pre>
	 * 
	 * The preceding example would be applicable for the "top" axis. It would be
	 * built such as with the following:
	 * 
	 * <pre> 
	 * {@code
	 * List<String> period = Arrays.asList("Jan", "Feb", "Mar");
	 * List<String> scenario = Arrays.asList("Actual");
	 * List<List<String>> test = Arrays.asList(period, scenario);
	 * }
	 * </pre>
	 *
	 * In testing it seems that the second List can be defined with three
	 * elements (three copies of "Actual") and the output will still be correct.
	 * At this point I can't tell if that's a glitch in the implementation or my
	 * understanding.
	 *
	 */
	private List<List<String>> members;

	private DimensionMembers() {
	}

	/**
	 * Convenience invocation, assumes that each provided member is also the
	 * dimension name.
	 * 
	 * @param members the dimension names
	 */
	public DimensionMembers(List<String> members) {
		this(members, members);
	}

	public DimensionMembers(List<String> dimensions, List<String> members) {
		this.dimensions = dimensions;
		this.members = new ArrayList<List<String>>();
		for (String member : members) {
			this.members.add(Arrays.asList(member));
		}
	}

	public static DimensionMembers of(List<String> dimensions, List<List<String>> members) {
		DimensionMembers dm = new DimensionMembers();
		dm.dimensions = dimensions;
		dm.members = members;
		return dm;
	}

	/**
	 * Each row of the outer list (the first List) represents a row in the axis.
	 * The elements of the inner list
	 * 
	 * @param members the member axes to transform into this object
	 * @return a new dimension members object for the members
	 */
	public static DimensionMembers of(List<List<String>> members) {
		DimensionMembers dm = new DimensionMembers();
		dm.dimensions = null;
		dm.members = members;
		return dm;
	}

	// ??: Means DM will be one dimension with the given members?
	public static DimensionMembers of(String... members) {
		return new DimensionMembers(null, Arrays.asList(members));
	}

	public List<String> getDimensions() {
		return dimensions;
	}

	public void setDimensions(List<String> dimensions) {
		this.dimensions = dimensions;
	}

	public List<List<String>> getMembers() {
		return members;
	}

	public void setMembers(List<List<String>> members) {
		this.members = members;
	}

	public DimensionMembers withMemberReplacement(String oldMember, String newDefinition) {
		for (List<String> memberList : members) {
			for (int index = 0; index < memberList.size(); index++) {
				if (memberList.get(index).equals(oldMember)) {
					System.out.println("Replacing " + oldMember + " with " + newDefinition);
					memberList.set(index, newDefinition);
				}
			}
		}
		return null;
	}

}
