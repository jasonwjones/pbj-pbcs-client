package com.jasonwjones.pbcs.util;

import com.jasonwjones.pbcs.client.PbcsDimension;
import com.jasonwjones.pbcs.client.PbcsMember;
import com.jasonwjones.pbcs.client.PbcsPlanType;

import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Queue;

/**
 * Provides a generic way to walk the outline of a given plan.
 */
public class PlanTypeWalker {

    private PlanTypeWalker() {}

    /**
     * Walks all the dimensions in the given plan, calling the visitor for various events when starting to process the
     * overall plan, dimensions, and members.
     *
     * @param planType the plan to walk
     * @param visitor the visitor delegate to call
     */
    public static void walk(PbcsPlanType planType, Visitor visitor) {
        visitor.startPlan(planType);

        for (PbcsDimension dimension : planType.getDimensions()) {
            if (visitor.startDimension(dimension) == MemberVisitResult.CONTINUE) {
                Queue<PbcsMember> members = new ArrayDeque<>();
                members.add(dimension.getRoot());

                while (!members.isEmpty()) {
                    PbcsMember current = members.remove();
                    if (visitor.visitMember(planType, current) == MemberVisitResult.CONTINUE) {
                        members.addAll(current.getChildren());
                    }
                }

                visitor.endDimension(dimension);
            }
        }
        visitor.endPlan(planType);
    }

    /**
     * A visitor with events that are called for various items being processed.
     */
    public interface Visitor {

        void startPlan(PbcsPlanType plan);

        void endPlan(PbcsPlanType plan);

        MemberVisitResult startDimension(PbcsDimension dimension);

        void endDimension(PbcsDimension dimension);

        MemberVisitResult visitMember(PbcsPlanType planType, PbcsMember member);

    }

    public enum MemberVisitResult {

        CONTINUE,

        TERMINATE

    }

    /**
     * A base class that can be extended by those implementing the Visitor interface. All methods are stubbed out so
     * that implementers only need to worry about method they care about.
     */
    public abstract static class AbstractVisitor implements Visitor {

        @Override
        public void startPlan(PbcsPlanType plan) {
        }

        @Override
        public void endPlan(PbcsPlanType plan) {
        }

        @Override
        public MemberVisitResult startDimension(PbcsDimension dimension) {
            return MemberVisitResult.CONTINUE;
        }

        @Override
        public void endDimension(PbcsDimension dimension) {
        }

        @Override
        public MemberVisitResult visitMember(PbcsPlanType planType, PbcsMember member) {
            return MemberVisitResult.CONTINUE;
        }

    }

    public static class PrinterVisitor extends AbstractVisitor implements Visitor {

        private final PrintStream printStream;

        private static final int SPACES_PER_LEVEL = 4;

        public PrinterVisitor() {
            this(System.out);
        }

        public PrinterVisitor(PrintStream printStream) {
            this.printStream = printStream;
        }

        @Override
        public MemberVisitResult visitMember(PbcsPlanType planType, PbcsMember member) {
            int spaces = (member.getGeneration() - 1) * SPACES_PER_LEVEL;
            printStream.println(space(spaces) + member.getName());
            return MemberVisitResult.CONTINUE;
        }

        private static String space(int size) {
            return String.join("", Collections.nCopies(size, " "));
        }

    }

}