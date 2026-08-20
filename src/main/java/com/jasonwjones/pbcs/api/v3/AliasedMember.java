package com.jasonwjones.pbcs.api.v3;

import java.util.List;

/**
 * Maps a single node of the member tree returned by the REST API's Get Dimension Details endpoint
 * ({@code GET applications/{app}/plantypes/{cube}/dimensions/{dimName}}) when requested with
 * {@code fields=name,alias,children}, optionally together with an {@code aliasTableName} query parameter.
 * The same shape represents every level of the tree: the dimension root and every descendant member.
 *
 * <p>{@link #getAlias()} is present only when the member has an alias in the requested table that differs
 * from its name; members without such an alias omit the field entirely (it deserializes as null here)
 * rather than repeating the member name.
 */
public class AliasedMember {

	private String name;

	private String alias;

	private List<AliasedMember> children;

	/**
	 * Constructs an empty instance for deserialization.
	 */
	public AliasedMember() {
	}

	/**
	 * Gets the member's name.
	 *
	 * @return the member name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the member's name.
	 *
	 * @param name the member name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the member's alias in the requested alias table, or null if it has none there (or none that
	 * differs from its name).
	 *
	 * @return the alias, may be null
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * Sets the member's alias.
	 *
	 * @param alias the alias
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * Gets this member's children.
	 *
	 * @return the children, may be null if this member has none
	 */
	public List<AliasedMember> getChildren() {
		return children;
	}

	/**
	 * Sets this member's children.
	 *
	 * @param children the children
	 */
	public void setChildren(List<AliasedMember> children) {
		this.children = children;
	}

}
