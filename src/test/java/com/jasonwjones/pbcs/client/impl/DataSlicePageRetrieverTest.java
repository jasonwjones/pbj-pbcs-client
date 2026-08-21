package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.api.v3.dataslices.DataSlice;
import com.jasonwjones.pbcs.api.v3.dataslices.DimensionMembers;
import com.jasonwjones.pbcs.api.v3.dataslices.ExportDataSlice;
import com.jasonwjones.pbcs.api.v3.dataslices.GridDefinition;
import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

public class DataSlicePageRetrieverTest {

    private final ExecutorService retrieveExecutor = Executors.newFixedThreadPool(3);
    private final ExecutorService callerExecutor = Executors.newSingleThreadExecutor();

    @After
    public void tearDown() {
        retrieveExecutor.shutdownNow();
        callerExecutor.shutdownNow();
    }

    @Test
    public void executesPagesConcurrentlyAndStitchesThemInRequestOrder() throws Exception {
        PbcsRetrieveOptionsImpl options = options(4, 3);
        CountDownLatch allStarted = new CountDownLatch(3);
        CountDownLatch release = new CountDownLatch(1);
        AtomicInteger active = new AtomicInteger();
        AtomicInteger maximumActive = new AtomicInteger();

        Future<DataSlice> resultFuture = callerExecutor.submit(() -> DataSlicePageRetriever.retrieve(request(6), options, page -> {
            int activeRequests = active.incrementAndGet();
            maximumActive.accumulateAndGet(activeRequests, Math::max);
            allStarted.countDown();
            await(release);
            active.decrementAndGet();
            return slice(firstRow(page));
        }));

        assertThat(allStarted.await(2, TimeUnit.SECONDS), is(true));
        release.countDown();
        DataSlice result = resultFuture.get(2, TimeUnit.SECONDS);

        assertThat(result.getRows().stream().map(row -> row.getHeaders().get(0)).toList(), contains("R0", "R2", "R4"));
        assertThat(maximumActive.get(), is(3));
    }

    @Test
    public void limitsConcurrencyForOneRetrieve() throws Exception {
        PbcsRetrieveOptionsImpl options = options(4, 2);
        CountDownLatch twoStarted = new CountDownLatch(2);
        CountDownLatch release = new CountDownLatch(1);
        AtomicInteger active = new AtomicInteger();
        AtomicInteger maximumActive = new AtomicInteger();

        Future<DataSlice> resultFuture = callerExecutor.submit(() -> DataSlicePageRetriever.retrieve(request(6), options, page -> {
            int activeRequests = active.incrementAndGet();
            maximumActive.accumulateAndGet(activeRequests, Math::max);
            twoStarted.countDown();
            await(release);
            active.decrementAndGet();
            return slice(firstRow(page));
        }));

        assertThat(twoStarted.await(2, TimeUnit.SECONDS), is(true));
        assertThat(maximumActive.get(), is(2));
        release.countDown();
        resultFuture.get(2, TimeUnit.SECONDS);
    }

    @Test
    public void singlePageDoesNotSubmitToExecutor() {
        PbcsRetrieveOptionsImpl options = options(100, 3);
        AtomicInteger executorCalls = new AtomicInteger();
        options.setRetrieveExecutor(command -> executorCalls.incrementAndGet());

        DataSlice result = DataSlicePageRetriever.retrieve(request(2), options, page -> slice(firstRow(page)));

        assertThat(executorCalls.get(), is(0));
        assertThat(result.getRows().get(0).getHeaders().get(0), is("R0"));
    }

    private PbcsRetrieveOptionsImpl options(int maxCells, int concurrency) {
        PbcsRetrieveOptionsImpl options = new PbcsRetrieveOptionsImpl();
        options.setMaxCellsPerRetrieve(maxCells);
        options.setMaxConcurrentRetrieveRequests(concurrency);
        options.setRetrieveExecutor(retrieveExecutor);
        return options;
    }

    private ExportDataSlice request(int rows) {
        List<DimensionMembers> rowDefinitions = new ArrayList<>();
        for (int row = 0; row < rows; row++) {
            rowDefinitions.add(new DimensionMembers(Collections.singletonList("Row"), Collections.singletonList("R" + row)));
        }
        GridDefinition grid = new GridDefinition(Collections.singletonList("POV"),
                Collections.singletonList(new DimensionMembers(Collections.singletonList("Column"), Collections.singletonList("C0"))),
                rowDefinitions);
        return new ExportDataSlice(grid);
    }

    private DataSlice slice(String header) {
        return new DataSlice(Collections.singletonList("POV"), Collections.singletonList(Collections.singletonList("C0")),
                new ArrayList<>(Collections.singletonList(new DataSlice.HeaderDataRow(header, "1"))));
    }

    private String firstRow(ExportDataSlice page) {
        return page.getGridDefinition().getRows().get(0).getMembers().get(0).get(0);
    }

    private void await(CountDownLatch release) {
        try {
            release.await(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AssertionError(e);
        }
    }

}
