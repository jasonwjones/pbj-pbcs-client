package com.jasonwjones.pbcs.client;

import java.util.List;
import java.util.function.Function;

public interface PovGrid<E> extends Grid<E> {

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