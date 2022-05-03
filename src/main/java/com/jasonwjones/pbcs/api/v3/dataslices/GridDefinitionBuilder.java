package com.jasonwjones.pbcs.api.v3.dataslices;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class GridDefinitionBuilder {

	private List<String> povMembers;

	private List<DimensionMembers> leftMembers;

	private List<DimensionMembers> topMembers;

	public GridDefinitionBuilder() {
		this.povMembers = new ArrayList<String>();
		this.leftMembers = new ArrayList<DimensionMembers>();
		this.topMembers = new ArrayList<DimensionMembers>();
	}

	public GridDefinitionBuilder pov(String member) {
		povMembers.add(member);
		return this;
	}

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

	public GridDefinitionBuilder pov(Collection<String> members) {
		povMembers.addAll(members);
		return this;
	}

	public GridDefinitionBuilder left(List<String> members) {
		leftMembers.add(new DimensionMembers(null, members));
		return this;
	}

	public GridDefinitionBuilder top(String... members) {
		topMembers.add(DimensionMembers.of(members));
		return this;
	}

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

	public GridDefinitionBuilder topWithArraysNatural(String[][] data) {
		return topWithListsNatural(toLists(data));
	}

	public GridDefinitionBuilder leftWithLists(List<List<String>> memberLists) {
		return withLists(leftMembers, memberLists);
	}

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

	public GridDefinitionBuilder auto(Collection<String> members, String left, String top) {
		Set<String> memberSet = new LinkedHashSet<String>(members);
		memberSet.remove(left);
		memberSet.remove(top);
		pov(memberSet);
		left(left);
		top(top);
		return this;
	}

	public GridDefinitionBuilder removePov(String... pov) {
		povMembers.removeAll(toList(pov));
		return this;
	}

	public GridDefinitionBuilder pivot() {
		List<DimensionMembers> temp = leftMembers;
		leftMembers = topMembers;
		topMembers = temp;
		return this;
	}

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