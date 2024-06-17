package com.jasonwjones.pbcs.client;

import java.util.Deque;
import java.util.LinkedList;
import java.util.stream.Collectors;

public interface PbcsObject {

    /**
     * Gets the name of this object. For example, a {@link PbcsApplication} object would return
     * the application name, such as "Vision". A {@link PbcsJobDefinition} may return a name such
     * <code>RefreshCube</code> or <code>calcall</code>.
     *
     * @return the name of this object
     */
    String getName();

    /**
     * Returns a fully- (or at least significantly) qualified name for this object. The default implementation will
     * walk the hierarchy of parents for this object all the way up to the client and concatenate the results of
     * {@link #getName()} with a period.
     *
     * @return a qualified name for this object
     */
    default String getQualifiedName() {
        Deque<PbcsObject> parents = new LinkedList<>();
        PbcsObject current = this;
        while (current != null) {
            parents.addFirst(current);
            current = current.getParent();
        }

        return parents.stream()
                .map(PbcsObject::getName)
                .collect(Collectors.joining("."));
    }

    /**
     * Returns the parent of this object, which may be another member, plan, application, client, or null if there is no
     * logical parent. Interfaces that extend this one provide covariant return types that are more specific than a
     * <code>PbcsObject</code>. For example, a plan will return its application as a parent.
     *
     * @return the parent of this object
     */
    PbcsObject getParent();

    /**
     * Returns the type of object this is. This construct is somewhat a blend between "official"
     * EPM objects and "synthetic" objects that are modeled in this library.
     *
     * @return the object type
     */
    PbcsObjectType getObjectType(); // look to refactor to just getType()

}