package com.jasonwjones.pbcs.api.v3.dataslices;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * A fluent builder for constructing a {@link GridDefinition}, specifying its POV, top axis, and left axis
 * members incrementally.
 */
public class GridDefinitionBuilder {

	private List<String> povMembers;

	private List<DimensionMembers> leftMembers;

	private List<DimensionMembers> topMembers;

	/**
	 * Constructs an empty builder.
	 */
	public GridDefinitionBuilder() {
		this.povMembers = new ArrayList<String>();
		this.leftMembers = new ArrayList<DimensionMembers>();
		this.topMembers = new ArrayList<DimensionMembers>();
	}

	/**
	 * Adds a single member to the POV.
	 *
	 * @param member the member to add
	 * @return the builder
	 */
	public GridDefinitionBuilder pov(String member) {
		povMembers.add(member);
		return this;
	}

	/**
	 * Adds all non-null members from the given rows to the POV.
	 *
	 * @param members the members to add
	 * @return the builder
	 */
	public GridDefinitionBuilder pov(String[][] members) {
		List<String> nonNullMembers = new ArrayList<String>();
		for (String[] row : members) {
			for (String item : row) {
				if (item != null) {
					nonNullMembers.add(item);
				}
			}
		}
		return pov(nonNullMembers);
	}

	/**
	 * Adds all the given members to the POV.
	 *
	 * @param members the members to add
	 * @return the builder
	 */
	public GridDefinitionBuilder pov(Collection<String> members) {
		povMembers.addAll(members);
		return this;
	}

	/**
	 * Adds a dimension to the left axis with the given members, one per row.
	 *
	 * @param members the members for the new left axis dimension
	 * @return the builder
	 */
	public GridDefinitionBuilder left(List<String> members) {
		leftMembers.add(new DimensionMembers(null, members));
		return this;
	}

	/**
	 * Adds a dimension to the top axis with the given members.
	 *
	 * @param members the members for the new top axis dimension
	 * @return the builder
	 */
	public GridDefinitionBuilder top(String... members) {
		topMembers.add(DimensionMembers.of(members));
		return this;
	}

	/**
	 * Adds a dimension to the top axis with the given members, one per row.
	 *
	 * @param members the members for the new top axis dimension
	 * @return the builder
	 */
	public GridDefinitionBuilder top(List<String> members) {
		topMembers.add(new DimensionMembers(null, members));
		return this;
	}

	/**
	 * Assumes data is in 'outer to inter' orientation, i.e., you might have two lists such as
	 * [FY18, Jan] and [FY19, Feb]
	 *
	 * @param memberLists the member lists
	 * @return the builder
	 */
	public GridDefinitionBuilder topWithLists(List<List<String>> memberLists) {
		return withLists(topMembers, memberLists);
	}

	/**
	 * Assumes that the outer list is dimensions and the inner list is the contents of a row, such as a simple data
	 * structure for the 'top' axis. For example, the incoming data may be:
	 *
	 * <pre>
	 * [                FY18][                FY18][                FY19]
     * [                 Jan][                 Feb][                  Q1]
     * </pre>
     *
     * It will be transformed into:
     *
     * <pre>
     * [                FY18][                 Jan]
     * [                FY18][                 Feb]
     * [                FY19][                  Q1]
     * </pre>
     *
     * And it will therefore be suitable for us with {@link #topWithLists(List)}, which is the
     * actual implementing method
	 *
	 * @param data the data
	 * @return a value
	 */
	public GridDefinitionBuilder topWithListsNatural(List<List<String>> data) {
		List<List<String>> outerToInner = new ArrayList<List<String>>();

		for (int col = 0; col < data.get(0).size(); col++) {
			List<String> current = new ArrayList<String>();
			for (int row = 0; row < data.size(); row++) {
				String cell = data.get(row).get(col);
				current.add(cell);
			}
			outerToInner.add(current);
		}

		System.out.println("Was given:");
		ArrayUtils.printLists(data);

		System.out.println("Became:");
		ArrayUtils.printLists(outerToInner);

		return topWithLists(outerToInner);
	}

	/**
	 * Array-based convenience form of {@link #topWithListsNatural(List)}.
	 *
	 * @param data the data, in natural (row-major) orientation
	 * @return the builder
	 */
	public GridDefinitionBuilder topWithArraysNatural(String[][] data) {
		return topWithListsNatural(toLists(data));
	}

	/**
	 * Adds one left axis dimension per given list, using each list's contents as that dimension's rows.
	 *
	 * @param memberLists the member lists, one per left axis dimension
	 * @return the builder
	 */
	public GridDefinitionBuilder leftWithLists(List<List<String>> memberLists) {
		return withLists(leftMembers, memberLists);
	}

	/**
	 * Array-based convenience form of {@link #leftWithLists(List)}.
	 *
	 * @param memberLists the member lists, one per left axis dimension
	 * @return the builder
	 */
	public GridDefinitionBuilder leftWithArrays(String[][] memberLists) {
		return leftWithLists(toLists(memberLists));
	}

	private GridDefinitionBuilder withLists(List<DimensionMembers> dimMembers, List<List<String>> memberLists) {
		for (List<String> memberList : memberLists) {
			dimMembers.add(new DimensionMembers(null, memberList));
		}
		return this;
	}

	/**
	 * Allows to specify the contents of the left axis columns, such as Q1, Final (note that each
	 * item represents different dimension)
	 *
	 * @param members the members
	 * @return the builder
	 */
	public GridDefinitionBuilder left(String... members) {
		leftMembers.add(DimensionMembers.of(members));
		return this;
	}

	/**
	 * Inserts the given members at the start of every existing left axis dimension.
	 *
	 * @param members the members to insert
	 * @return the builder
	 */
	public GridDefinitionBuilder leftAdd(String... members) {
		List<String> items = toList(members);
		for (DimensionMembers dm : leftMembers) {
			dm.addFirst(items);
		}
		return this;
	}

	/**
	 * Expands the members on the first dimension members item and creates one if there aren't any.
	 *
	 * @param members the members
	 * @return the builder
	 */
	public GridDefinitionBuilder leftAddToFirst(String... members) {
		List<String> items = toList(members);
		if (leftMembers.isEmpty()) {
			leftMembers.add(DimensionMembers.ofSingleDimension(members));
		} else {
			leftMembers.get(0).addToFirst(items);
		}
		return this;
	}

	/**
	 * Automatically lays out the given members: the first becomes the sole top axis member, the second the
	 * sole left axis member, and the rest are added to the POV.
	 *
	 * @param members the members to lay out, at least 2
	 * @return the builder
	 * @throws IllegalArgumentException if fewer than 2 members are given
	 */
	public GridDefinitionBuilder auto(Collection<String> members) {
		if (members.size() < 2) {
			throw new IllegalArgumentException("Auto layout requires at least 2 members");
		}
		List<String> memberList = new ArrayList<String>(members);
		top(memberList.get(0));
		left(memberList.get(1));
		pov(memberList.subList(2, memberList.size()));
		return this;
	}

	/**
	 * Automatically lays out the given members, using the specified members as the sole left and top axis
	 * members and adding the rest to the POV.
	 *
	 * @param members the full set of members
	 * @param left the member to use as the sole left axis member
	 * @param top the member to use as the sole top axis member
	 * @return the builder
	 */
	public GridDefinitionBuilder auto(Collection<String> members, String left, String top) {
		Set<String> memberSet = new LinkedHashSet<String>(members);
		memberSet.remove(left);
		memberSet.remove(top);
		pov(memberSet);
		left(left);
		top(top);
		return this;
	}

	/**
	 * Removes the given members from the POV.
	 *
	 * @param pov the members to remove
	 * @return the builder
	 */
	public GridDefinitionBuilder removePov(String... pov) {
		povMembers.removeAll(toList(pov));
		return this;
	}

	/**
	 * Swaps the top and left axis definitions.
	 *
	 * @return the builder
	 */
	public GridDefinitionBuilder pivot() {
		List<DimensionMembers> temp = leftMembers;
		leftMembers = topMembers;
		topMembers = temp;
		return this;
	}

	/**
	 * Builds the grid definition from the POV, top axis, and left axis specified so far.
	 *
	 * @return the resulting grid definition
	 */
	public GridDefinition build() {
		GridDefinition gridDefinition = new GridDefinition();
		gridDefinition.setPov(new DimensionMembers(null, povMembers));
		//gridDefinition.setPov(new DimensionMembers(povMembers));
		gridDefinition.setRows(leftMembers);
		gridDefinition.setColumns(topMembers);
		return gridDefinition;
	}

	private static List<String> toList(String[] items) {
		List<String> list = new ArrayList<String>();
		for (String item : items) {
			list.add(item);
		}
		return list;
	}

	private static List<List<String>> toLists(String[][] data) {
		List<List<String>> arrayLists = new ArrayList<List<String>>();
		for (String[] row : data) {
			List<String> rowList = new ArrayList<String>();
			for (String item : row) {
				rowList.add(item);
			}
			arrayLists.add(rowList);
		}
		return arrayLists;
	}

}