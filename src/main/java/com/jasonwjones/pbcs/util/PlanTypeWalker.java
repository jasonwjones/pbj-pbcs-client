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

    /**
     * Walks all the dimensions in the given plan using the given options, calling the visitor for various
     * events when starting to process the overall plan, dimensions, and members.
     *
     * @param planType the plan to walk
     * @param visitor the visitor delegate to call
     * @param options the traversal options to use
     * @return true if the walk completed within the internal timeout, false otherwise
     */
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

    /**
     * A {@link Runnable} that walks a single dimension breadth-first, visiting the dimension's root member
     * and then its children level by level.
     */
    public static class BreadthFirstDimensionProcessor implements Runnable {

        /**
         * The plan type being walked.
         */
        protected final PbcsPlanType planType;

        /**
         * The dimension being walked.
         */
        protected final PbcsDimension dimension;

        /**
         * The visitor to call as members are walked.
         */
        protected final Visitor visitor;

        /**
         * Constructs an instance for the given plan type, dimension, and visitor.
         *
         * @param planType the plan type being walked
         * @param dimension the dimension to walk
         * @param visitor the visitor to call as members are walked
         */
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

    /**
     * A {@link Runnable} that walks a single dimension depth-first, fully visiting each member's descendants
     * before moving to the next sibling.
     */
    public static class DepthFirstDimensionProcessor extends BreadthFirstDimensionProcessor {

        /**
         * Constructs an instance for the given plan type, dimension, and visitor.
         *
         * @param planType the plan type being walked
         * @param dimension the dimension to walk
         * @param visitor the visitor to call as members are walked
         */
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

        /**
         * Called once, before any dimension is walked.
         *
         * @param plan the plan being walked
         */
        void startPlan(PbcsPlanType plan);

        /**
         * Called once, after all dimensions have been walked.
         *
         * @param plan the plan being walked
         */
        void endPlan(PbcsPlanType plan);

        /**
         * Called when starting to walk a dimension.
         *
         * @param dimension the dimension about to be walked
         * @return whether the walk should continue into this dimension
         */
        MemberVisitResult startDimension(PbcsDimension dimension);

        /**
         * Called after a dimension has been fully walked.
         *
         * @param dimension the dimension that was walked
         */
        void endDimension(PbcsDimension dimension);

        /**
         * Called for each member visited.
         *
         * @param planType the plan type being walked
         * @param member the member being visited
         * @return whether the walk should continue into this member's children
         */
        MemberVisitResult visitMember(PbcsPlanType planType, PbcsMember member);

    }

    /**
     * The result a {@link Visitor} returns to control whether a walk continues.
     */
    public enum MemberVisitResult {

        /**
         * Continue the walk.
         */
        CONTINUE,

        /**
         * Stop walking further into the current dimension/member.
         */
        TERMINATE

    }

    /**
     * A base class that can be extended by those implementing the Visitor interface. All methods are stubbed out so
     * that implementers only need to worry about method they care about.
     */
    public abstract static class AbstractVisitor implements Visitor {

        /**
         * Constructs an instance of this abstract visitor.
         */
        protected AbstractVisitor() {
        }

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

    /**
     * Options controlling how a plan type is walked: which dimensions to include, the traversal order, and
     * the degree of parallelism.
     */
    public static class Options {

        private TraversalType traversalType = TraversalType.DEPTH_FIRST;

        private List<String> dimensionNames = new ArrayList<>();

        private int threads = Runtime.getRuntime().availableProcessors();

        /**
         * Constructs an instance with the default options: every dimension, depth-first, using a thread per
         * available processor.
         */
        public Options() {
        }

        /**
         * Gets the traversal order to use.
         *
         * @return the traversal type
         */
        public TraversalType getTraversalType() {
            return traversalType;
        }

        /**
         * Sets the traversal order to use.
         *
         * @param traversalType the traversal type
         */
        public void setTraversalType(TraversalType traversalType) {
            this.traversalType = traversalType;
        }

        /**
         * Gets the names of the dimensions to walk. An empty list means all dimensions.
         *
         * @return the dimension names
         */
        public List<String> getDimensionNames() {
            return dimensionNames;
        }

        /**
         * Sets the names of the dimensions to walk.
         *
         * @param dimensionNames the dimension names
         */
        public void setDimensionNames(List<String> dimensionNames) {
            this.dimensionNames = dimensionNames;
        }

        /**
         * Gets the number of threads to use, with one dimension processed per thread.
         *
         * @return the number of threads
         */
        public int getThreads() {
            return threads;
        }

        /**
         * Sets the number of threads to use.
         *
         * @param threads the number of threads
         */
        public void setThreads(int threads) {
            this.threads = threads;
        }

    }

    /**
     * The order in which a dimension's members are visited.
     */
    public enum TraversalType {

        /**
         * Visit members level by level.
         */
        BREADTH_FIRST,

        /**
         * Fully visit each member's descendants before moving to the next sibling.
         */
        DEPTH_FIRST

    }

}