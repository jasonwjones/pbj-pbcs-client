package com.jasonwjones.pbcs.client;

import java.util.List;
import java.util.function.Function;

/**
 * A {@link Grid} that additionally carries a POV (point of view) for the dimensions not represented on
 * either axis.
 *
 * @param <E> the cell type
 */
public interface PovGrid<E> extends Grid<E> {

    /**
     * Gets the POV cells for this grid.
     *
     * @return the POV cells
     */
    List<E> getPov();

    /**
     * Create a copy of this grid using the supplied transform function.
     *
     * @param conversion the conversion function
     * @return a new grid
     * @param <T> the result type for the new grid
     */
    <T> PovGrid<T> copyOf(Function<E, T> conversion);

}