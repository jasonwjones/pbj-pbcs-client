package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.api.v3.dataslices.DataSlice;
import com.jasonwjones.pbcs.api.v3.dataslices.ExportDataSlice;
import com.jasonwjones.pbcs.api.v3.dataslices.GridDefinition;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.client.exceptions.PbcsClientException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;
import java.util.function.Function;

final class DataSlicePageRetriever {

    private DataSlicePageRetriever() {}

    static DataSlice retrieve(ExportDataSlice request, PbcsPlanType.RetrieveOptions options, Function<ExportDataSlice, DataSlice> export) {
        GridDefinition gridDefinition = request.getGridDefinition();
        int totalRequestedRows = gridDefinition.getRows().size();
        int numCellsPerRow = gridDefinition.getRows().get(0).getMembers().size() + gridDefinition.getColumns().size();
        int rowsPerRetrieve = Math.max(1, options.getMaxCellsPerRetrieve() / numCellsPerRow);
        int numPages = (int) Math.ceil((double) totalRequestedRows / rowsPerRetrieve);
        List<ExportDataSlice> pageRequests = pageRequests(request, rowsPerRetrieve, numPages);

        if (numPages == 1 || options.getMaxConcurrentRetrieveRequests() == 1) {
            List<DataSlice> slices = new ArrayList<>(numPages);
            for (ExportDataSlice pageRequest : pageRequests) {
                slices.add(export.apply(pageRequest));
            }
            return stitch(slices);
        }

        return retrieveConcurrent(pageRequests, options, export);
    }

    private static DataSlice retrieveConcurrent(List<ExportDataSlice> requests, PbcsPlanType.RetrieveOptions options,
                                                Function<ExportDataSlice, DataSlice> export) {
        int concurrency = Math.min(options.getMaxConcurrentRetrieveRequests(), requests.size());
        CompletionService<IndexedSlice> completionService = new ExecutorCompletionService<>(options.getRetrieveExecutor());
        List<Future<IndexedSlice>> futures = new ArrayList<>(requests.size());
        DataSlice[] slices = new DataSlice[requests.size()];
        int nextPage = 0;

        try {
            while (nextPage < concurrency) {
                futures.add(submit(completionService, requests, nextPage++, export));
            }
            for (int completed = 0; completed < requests.size(); completed++) {
                IndexedSlice indexedSlice = completionService.take().get();
                slices[indexedSlice.index] = indexedSlice.slice;
                if (nextPage < requests.size()) {
                    futures.add(submit(completionService, requests, nextPage++, export));
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            cancel(futures);
            throw new PbcsClientException("Interrupted while retrieving data slice pages", e);
        } catch (ExecutionException e) {
            cancel(futures);
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) throw (RuntimeException) cause;
            throw new PbcsClientException("Unable to retrieve data slice page", cause);
        }

        return stitch(Arrays.asList(slices));
    }

    private static Future<IndexedSlice> submit(CompletionService<IndexedSlice> completionService,
                                               List<ExportDataSlice> requests, int index,
                                               Function<ExportDataSlice, DataSlice> export) {
        return completionService.submit(() -> new IndexedSlice(index, export.apply(requests.get(index))));
    }

    private static void cancel(List<? extends Future<?>> futures) {
        for (Future<?> future : futures) future.cancel(true);
    }

    private static List<ExportDataSlice> pageRequests(ExportDataSlice request, int rowsPerRetrieve, int numPages) {
        GridDefinition original = request.getGridDefinition();
        List<ExportDataSlice> requests = new ArrayList<>(numPages);
        for (int page = 0; page < numPages; page++) {
            int fromIndex = page * rowsPerRetrieve;
            int toIndex = Math.min(fromIndex + rowsPerRetrieve, original.getRows().size());

            GridDefinition pageGrid = new GridDefinition();
            pageGrid.setPov(original.getPov());
            pageGrid.setColumns(original.getColumns());
            pageGrid.setRows(new ArrayList<>(original.getRows().subList(fromIndex, toIndex)));
            pageGrid.setSuppressMissingBlocks(original.isSuppressMissingBlocks());
            pageGrid.setSuppressMissingRows(original.isSuppressMissingRows());
            pageGrid.setSuppressMissingColumns(original.isSuppressMissingColumns());

            ExportDataSlice pageRequest = new ExportDataSlice(pageGrid);
            pageRequest.setExportPlanningData(request.isExportPlanningData());
            requests.add(pageRequest);
        }
        return requests;
    }

    private static DataSlice stitch(List<DataSlice> slices) {
        DataSlice primarySlice = slices.get(0);
        for (int sliceIndex = 1; sliceIndex < slices.size(); sliceIndex++) {
            primarySlice.getRows().addAll(slices.get(sliceIndex).getRows());
        }
        return primarySlice;
    }

    private static final class IndexedSlice {
        private final int index;
        private final DataSlice slice;

        private IndexedSlice(int index, DataSlice slice) {
            this.index = index;
            this.slice = slice;
        }
    }

}
