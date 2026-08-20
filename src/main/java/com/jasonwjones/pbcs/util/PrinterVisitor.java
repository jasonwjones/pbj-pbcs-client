package com.jasonwjones.pbcs.util;

import com.jasonwjones.pbcs.client.PbcsMember;
import com.jasonwjones.pbcs.client.PbcsPlanType;

import java.io.PrintStream;
import java.util.Collections;

/**
 * A {@link PlanTypeWalker.Visitor} that prints each visited member's name to a stream, indented according
 * to its generation.
 */
public class PrinterVisitor extends PlanTypeWalker.AbstractVisitor implements PlanTypeWalker.Visitor {

    private final PrintStream printStream;

    private static final int SPACES_PER_LEVEL = 4;

    /**
     * Constructs an instance printing to {@link System#out}.
     */
    public PrinterVisitor() {
        this(System.out);
    }

    /**
     * Constructs an instance printing to the given stream.
     *
     * @param printStream the stream to print member names to
     */
    public PrinterVisitor(PrintStream printStream) {
        this.printStream = printStream;
    }

    @Override
    public PlanTypeWalker.MemberVisitResult visitMember(PbcsPlanType planType, PbcsMember member) {
        int spaces = (member.getGeneration() - 1) * SPACES_PER_LEVEL;
        printStream.println(space(spaces) + member.getName());
        return PlanTypeWalker.MemberVisitResult.CONTINUE;
    }

    private static String space(int size) {
        return String.join("", Collections.nCopies(size, " "));
    }

}