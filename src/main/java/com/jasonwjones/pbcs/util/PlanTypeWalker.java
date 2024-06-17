package com.jasonwjones.pbcs.util;

import com.jasonwjones.pbcs.client.PbcsDimension;
import com.jasonwjones.pbcs.client.PbcsMember;
import com.jasonwjones.pbcs.client.PbcsPlanType;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Provides a generic way to walk the outline of a given plan.
 */
public class PlanTypeWalker {

    /**
     * Default options to use when none are specified: will search every dimension using a number of threads equal to
     * the available processors with depth-first search.
     */
    public static final Options DEFAULT_OPTIONS = new Options();

    private PlanTypeWalker() {}

    /**
     * Walks all the dimensions in the given plan, calling the visitor for various events when starting to process the
     * overall plan, dimensions, and members.
     *
     * @param planType the plan to walk
     * @param visitor the visitor delegate to call
     * @return true the result from the executor
     */
    public static boolean walk(PbcsPlanType planType, Visitor visitor) {
        return walk(planType, visitor, DEFAULT_OPTIONS);
    }

    public static boolean walk(PbcsPlanType planType, Visitor visitor, Options options) {
        visitor.startPlan(planType);

        List<PbcsDimension> dimensions = new ArrayList<>();
        if (options.getDimensionNames() != null && !options.getDimensionNames().isEmpty()) {
            for (String dimensionName : options.getDimensionNames()) {
                dimensions.add(planType.getDimension(dimensionName));
            }
        } else {
            dimensions = planType.getDimensions();
        }

        ExecutorService executorService = Executors.newFixedThreadPool(options.getThreads());

        for (PbcsDimension dimension : dimensions) {
            Runnable runnable = options.getTraversalType() == TraversalType.BREADTH_FIRST ?
                    new BreadthFirstDimensionProcessor(planType, dimension, visitor) :
                    new DepthFirstDimensionProcessor(planType, dimension, visitor);
            executorService.submit(runnable);
        }

        try {
            executorService.shutdown();
            return executorService.awaitTermination(5, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
            return false;
        } finally {
            visitor.endPlan(planType);
        }

    }

    public static class BreadthFirstDimensionProcessor implements Runnable {

        protected final PbcsPlanType planType;

        protected final PbcsDimension dimension;

        protected final Visitor visitor;

        public BreadthFirstDimensionProcessor(PbcsPlanType planType, PbcsDimension dimension, Visitor visitor) {
            this.planType = planType;
            this.dimension = dimension;
            this.visitor = visitor;
        }

        @Override
        public void run() {
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

    }

    public static class DepthFirstDimensionProcessor extends BreadthFirstDimensionProcessor {

        public DepthFirstDimensionProcessor(PbcsPlanType planType, PbcsDimension dimension, Visitor visitor) {
            super(planType, dimension, visitor);
        }

        @Override
        public void run() {
            if (visitor.startDimension(dimension) == MemberVisitResult.CONTINUE) {
                process(dimension.getRoot());
            }
        }

        private void process(PbcsMember member) {
            if (visitor.visitMember(planType, member) == MemberVisitResult.CONTINUE) {
                for (PbcsMember child : member.getChildren()) {
                    process(child);
                }
            }
        }

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

    public static class Options {

        private TraversalType traversalType = TraversalType.DEPTH_FIRST;

        private List<String> dimensionNames = new ArrayList<>();

        private int threads = Runtime.getRuntime().availableProcessors();

        public TraversalType getTraversalType() {
            return traversalType;
        }

        public void setTraversalType(TraversalType traversalType) {
            this.traversalType = traversalType;
        }

        public List<String> getDimensionNames() {
            return dimensionNames;
        }

        public void setDimensionNames(List<String> dimensionNames) {
            this.dimensionNames = dimensionNames;
        }

        public int getThreads() {
            return threads;
        }

        public void setThreads(int threads) {
            this.threads = threads;
        }

    }

    public enum TraversalType {

        BREADTH_FIRST,

        DEPTH_FIRST

    }

}