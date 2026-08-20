package com.jasonwjones.pbcs.util;

import com.jasonwjones.pbcs.client.*;
import com.jasonwjones.pbcs.client.exceptions.PbcsClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * A local, in-memory cache of an application or plan type's full member outline, built by walking every
 * dimension's hierarchy once. Useful for repeated member lookups without re-hitting the REST API.
 */
public class Outline {

	private static final Logger logger = LoggerFactory.getLogger(Outline.class);

	/**
	 * Maps member names to a Member object -- which can be used to determine the dimension as
	 * needed, e.g. with {@link Member#getDimension()}
	 */
	private Map<String, Member> memberDimensions;

	private List<String> dimensionNames;

	private String app;

	/**
	 * Constructs an instance by walking every dimension of the given application.
	 *
	 * @param application the application to build the outline from
	 * @deprecated prefer {@link #Outline(PbcsPlanType)}, which scopes the outline to a plan type's dimensions
	 */
	@Deprecated
	public Outline(PbcsApplication application) {
		this.app = application.getName();

		memberDimensions = new HashMap<String, Member>();
		List<PbcsAppDimension> dimensions = application.getDimensions();
		//List<PbcsDimension> dimensions = Arrays.asList(application.getDimension("Period"));

		this.dimensionNames = new ArrayList<String>();

		for (PbcsDimension dimension : dimensions) {
			logger.info("Processing dimension {}", dimension.getName());
			dimensionNames .add(dimension.getName());

			PbcsMember rootMember = application.getMember(dimension.getName(), dimension.getName());
			logger.debug("{} has {} children", rootMember.getName(), rootMember.getChildren().size());

			Queue<PbcsMember> members = new ArrayDeque<PbcsMember>();
			members.add(rootMember);

			while (!members.isEmpty()) {
				PbcsMember current = members.remove();

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

	/**
	 * Gets all the members cached in this outline.
	 *
	 * @return the cached members
	 */
	public Collection<Member> getMembers() {
		return Collections.unmodifiableCollection(memberDimensions.values());
	}

	/**
	 * Constructs an instance by walking every dimension of the given plan type.
	 *
	 * @param planType the plan type to build the outline from
	 */
	public Outline(PbcsPlanType planType) {
		this.app = planType.getName();
		this.dimensionNames = new ArrayList<String>();

		memberDimensions = new HashMap<String, Member>();
		List<PbcsDimension> dimensions = planType.getDimensions();
		// List<PbcsDimension> dimensions = Arrays.asList(application.getDimension("Period"));

		for (PbcsDimension dimension : dimensions) {
			logger.info("Processing dimension {}", dimension.getName());
			this.dimensionNames.add(dimension.getName());
			PbcsMember rootMember = planType.getApplication().getMember(dimension.getName(), dimension.getName());
			logger.debug("{} has {} children", rootMember.getName(), rootMember.getChildren().size());

			Queue<PbcsMember> members = new ArrayDeque<PbcsMember>();
			members.add(rootMember);

			while (!members.isEmpty()) {
				PbcsMember current = members.remove();

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

	/**
	 * Constructs an instance by walking only the given dimensions of the given application.
	 *
	 * @param application the application to build the outline from
	 * @param planType a name used to label this outline (typically the plan type name) for error messages
	 * @param dimensionNames the dimensions to walk
	 */
	public Outline(PbcsApplication application, String planType, List<String> dimensionNames) {
		this.app = planType;
		this.dimensionNames = new ArrayList<String>();
		memberDimensions = new HashMap<String, Member>();

		for (String dimension : dimensionNames) {
			logger.info("Processing dimension {}", dimension);
			this.dimensionNames.add(dimension);
			PbcsMember rootMember = application.getMember(dimension, dimension);
			logger.debug("{} has {} children", rootMember.getName(), rootMember.getChildren().size());

			Queue<PbcsMember> members = new ArrayDeque<PbcsMember>();
			members.add(rootMember);

			while (!members.isEmpty()) {
				PbcsMember current = members.remove();

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

	/**
	 * Gets the dimension name for the given member.
	 *
	 * @param memberName the member name
	 * @return the dimension name
	 * @throws NullPointerException if the member is not cached in this outline
	 */
	public String getDimension(String memberName) {
		return memberDimensions.get(memberName).getDimension();
	}

	/**
	 * Gets the names of the dimensions in this outline.
	 *
	 * @return the dimension names
	 */
	public List<String> getDimensionNames() {
		return this.dimensionNames;
	}

	private int getGeneration(String member) {
		if (memberDimensions.containsKey(member)) {
			PbcsMember memberObject = memberDimensions.get(member).getMember();
			if (memberObject.getParentName() == null) {
				return 1;
			} else {
				return getGeneration(memberObject.getParentName()) + 1;
			}
		}
		throw new IllegalArgumentException("No such member " + member);
	}

	// just assume it's children for now
	/**
	 * Runs a query against the cached outline. Currently the only supported behavior is returning the
	 * member's children, regardless of the operation requested.
	 *
	 * @param operation the query operation (currently unused)
	 * @param memberName the member to query
	 * @return the member's children
	 * @throws PbcsClientException if the member is not cached in this outline
	 */
	public List<Member> executeQuery(String operation, String memberName) {
		Member member = memberDimensions.get(memberName);
		if (member != null) {
			return member.getChildren();
		} else {
			throw new PbcsClientException("Cached outline for " + app + " does not have a member named " + memberName);
		}
	}

	/**
	 * Gets the cached member with the given name.
	 *
	 * @param memberName the member name
	 * @return the cached member, or null if not present in this outline
	 */
	public Member getMember(String memberName) {
		return memberDimensions.get(memberName);
	}

	/**
	 * Wraps a {@link PbcsMember} cached in an {@link Outline}, providing generation lookups that use the
	 * outline's cache rather than making further REST calls.
	 */
	public class Member {

		private PbcsMember member;

		/**
		 * Constructs an instance wrapping the given member.
		 *
		 * @param member the member to wrap
		 */
		public Member(PbcsMember member) {
			this.member = member;
		}

		/**
		 * Gets the wrapped member.
		 *
		 * @return the wrapped member
		 */
		public PbcsMember getMember() {
			return member;
		}

		/**
		 * Gets the generation of this member, computed by walking up its cached parents.
		 *
		 * @return the generation
		 */
		public int getGeneration() {
			return Outline.this.getGeneration(member.getName());
		}

		/**
		 * Gets the level of this member.
		 *
		 * @return the level
		 */
		public int getLevel() {
			return member.getLevel();
		}

		/**
		 * Gets the name of this member.
		 *
		 * @return the member name
		 */
		public String getName() {
			return member.getName();
		}

		/**
		 * Gets the dimension name this member belongs to.
		 *
		 * @return the dimension name
		 */
		public String getDimension() {
			return member.getDimensionName();
		}

		/**
		 * Gets the name of this member's parent.
		 *
		 * @return the parent name, may be null
		 */
		public String getParent() {
			return member.getParentName();
		}

		/**
		 * Gets this member's children, resolved from the outline's cache.
		 *
		 * @return the children
		 */
		public List<Member> getChildren() {
			List<Member> children = new ArrayList<Member>();
			for (PbcsMember child : member.getChildren()) {
				Member member = memberDimensions.get(child.getName());
				children.add(member);
			}
			return children;
		}

	}

}