package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.client.PbcsMemberProperties;

import java.nio.file.FileVisitResult;

/**
 * Used when walking a dimension tree.
 */
public interface MemberVisitor {

    /**
     * Called for each member in the hierarchy.
     *
     * @param member the current member
     * @return a result (borrowed from the FileVisitor API) of whether to continue or stop
     */
    FileVisitResult preVisitMember(PbcsMemberProperties member);

}