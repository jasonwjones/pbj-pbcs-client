package com.jasonwjones.pbcs.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jasonwjones.pbcs.client.PbcsApplication;
import com.jasonwjones.pbcs.client.PbcsDimension;
import com.jasonwjones.pbcs.client.PbcsMemberProperties;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.client.exceptions.PbcsClientException;

public class Outline {

	private static final Logger logger = LoggerFactory.getLogger(Outline.class);

	/**
	 * Maps member names to a Member object -- which can be used to determine the dimension as
	 * needed, e.g. with {@link Member#getDimension()}
	 */
	private Map<String, Member> memberDimensions;

	private List<String> dimensionNames;

	private String app;

	@Deprecated
	public Outline(PbcsApplication application) {
		this.app = application.getName();

		memberDimensions = new HashMap<String, Member>();
		List<PbcsDimension> dimensions = application.getDimensions();
		//List<PbcsDimension> dimensions = Arrays.asList(application.getDimension("Period"));

		this.dimensionNames = new ArrayList<String>();
		
		for (PbcsDimension dimension : dimensions) {
			logger.info("Processing dimension {}", dimension.getName());
			dimensionNames .add(dimension.getName());

			PbcsMemberProperties rootMember = application.getMember(dimension.getName(), dimension.getName());
			logger.debug("{} has {} children", rootMember.getName(), rootMember.getChildren().size());

			Queue<PbcsMemberProperties> members = new ArrayDeque<PbcsMemberProperties>();
			members.add(rootMember);

			while (!members.isEmpty()) {
				PbcsMemberProperties current = members.remove();

				members.addAll(current.getChildren());
				logger.trace("Processing {}, has {} children, level: {} in dim {}", current.getName(), current.getChildren().size(), current.getLevel(), current.getDimensionName());
				boolean isShare = memberDimensions.containsKey(current.getName());

				if (!isShare) {
					Member member = new Member(current);
					memberDimensions.put(current.getName(), member);
				}
			}
		}

	}

	public Collection<Member> getMembers() {
		return Collections.unmodifiableCollection(memberDimensions.values());
	}
	
	public Outline(PbcsPlanType planType) {
		this.app = planType.getName();
		this.dimensionNames = new ArrayList<String>();

		memberDimensions = new HashMap<String, Member>();
		List<PbcsDimension> dimensions = planType.getDimensions();
		// List<PbcsDimension> dimensions = Arrays.asList(application.getDimension("Period"));

		for (PbcsDimension dimension : dimensions) {
			logger.info("Processing dimension {}", dimension.getName());
			this.dimensionNames.add(dimension.getName());
			PbcsMemberProperties rootMember = planType.getApplication().getMember(dimension.getName(), dimension.getName());
			logger.debug("{} has {} children", rootMember.getName(), rootMember.getChildren().size());

			Queue<PbcsMemberProperties> members = new ArrayDeque<PbcsMemberProperties>();
			members.add(rootMember);

			while (!members.isEmpty()) {
				PbcsMemberProperties current = members.remove();

				members.addAll(current.getChildren());
				logger.trace("Processing {}, has {} children, level: {} in dim {}", current.getName(), current.getChildren().size(), current.getLevel(), current.getDimensionName());
				boolean isShare = memberDimensions.containsKey(current.getName());

				if (!isShare) {
					Member member = new Member(current);
					memberDimensions.put(current.getName(), member);
				}
			}
		}
	}

	public String getDimension(String memberName) {
		return memberDimensions.get(memberName).getDimension();
	}
	
	public List<String> getDimensionNames() {
		return this.dimensionNames;
	}

	private int getGeneration(String member) {
		if (memberDimensions.containsKey(member)) {
			PbcsMemberProperties memberObject = memberDimensions.get(member).getMember();
			if (memberObject.getParentName() == null) {
				return 1;
			} else {
				return getGeneration(memberObject.getParentName()) + 1;
			}
		}
		throw new IllegalArgumentException("No such member " + member);
	}

	// just assume it's children for now
	public List<Member> executeQuery(String operation, String memberName) {
		Member member = memberDimensions.get(memberName);
		if (member != null) {
			return member.getChildren();
		} else {
			throw new PbcsClientException("Cached outline for " + app + " does not have a member named " + memberName);
		}
	}

	public Member getMember(String memberName) {
		return memberDimensions.get(memberName);
	}

	public class Member {

		private PbcsMemberProperties member;

		public Member(PbcsMemberProperties member) {
			this.member = member;
		}

		public PbcsMemberProperties getMember() {
			return member;
		}

		public int getGeneration() {
			return Outline.this.getGeneration(member.getName());
		}

		public int getLevel() {
			return member.getLevel();
		}

		public String getName() {
			return member.getName();
		}

		public String getDimension() {
			return member.getDimensionName();
		}

		public String getParent() {
			return member.getParentName();
		}

		public List<Member> getChildren() {
			List<Member> children = new ArrayList<Member>();
			for (PbcsMemberProperties child : member.getChildren()) {
				Member member = memberDimensions.get(child.getName());
				children.add(member);
			}
			return children;
		}

	}

}
