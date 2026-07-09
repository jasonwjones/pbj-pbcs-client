package com.jasonwjones.pbcs.util;

import com.jasonwjones.pbcs.client.PbcsMember;
import com.jasonwjones.pbcs.client.PbcsPlanType;

import java.io.PrintStream;
import java.util.Collections;

public class PrinterVisitor extends PlanTypeWalker.AbstractVisitor implements PlanTypeWalker.Visitor {

    private final PrintStream printStream;

    private static final int SPACES_PER_LEVEL = 4;

    public PrinterVisitor() {
        this(System.out);
    }

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