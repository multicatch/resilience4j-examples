package io.github.multicatch.resilience4j.bulkhead;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadRegistry;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadSemaphoreExample {
    public static void main(String[] args) {
        BulkheadConfig config = BulkheadConfig.custom()
                .maxConcurrentCalls(3)
                .build();
        BulkheadRegistry registry = BulkheadRegistry.of(config);
        Bulkhead bulkhead1 = registry.bulkhead("bulkhead1");

        ExecutorService executorService = Executors.newFixedThreadPool(8);
        for (int i = 0; i < 30; i++) {
            final int threadNo = i;
            executorService.submit(() -> {
                try {
                    executeBulkheadJob(threadNo, bulkhead1);
                } catch (Exception e) {
                    System.out.printf("Thread #%d failed.%n", threadNo);
                    e.printStackTrace();
                }
            });
        }

        executorService.shutdown();
    }

    private static void executeBulkheadJob(int threadNo, Bulkhead bulkhead) {
         bulkhead.executeRunnable(() -> {
            System.out.printf("Thread #%d: Started, waiting 1000 ms.%n", threadNo);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.printf("Thread #%d: Finishing.%n", threadNo);
        });
    }
}
