package com.jasonwjones.pbcs.client.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.jasonwjones.pbcs.client.PbcsMemberProperties;

public class PbcsMemberQueryImpl {

	public static List<String> level0Members(PbcsMemberProperties rootMember) {
		List<String> leafNodes = new ArrayList<String>();

		Queue<PbcsMemberProperties> memberQueue = new LinkedList<PbcsMemberProperties>();
		memberQueue.add(rootMember);

		while (!memberQueue.isEmpty()) {
			PbcsMemberProperties current = memberQueue.remove();
			if (current.isLeaf()) {
				leafNodes.add(current.getName());
			} else {
				memberQueue.addAll(current.getChildren());
			}
		}
		return leafNodes;
	}
}
