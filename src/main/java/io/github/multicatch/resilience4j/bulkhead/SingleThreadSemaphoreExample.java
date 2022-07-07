package io.github.multicatch.resilience4j.bulkhead;

import io.github.resilience4j.bulkhead.*;
import io.github.resilience4j.bulkhead.internal.InMemoryBulkheadRegistry;
import io.github.resilience4j.bulkhead.internal.SemaphoreBulkhead;

public class SingleThreadSemaphoreExample {
    public static void main(String[] args) {
        BulkheadConfig config = BulkheadConfig.custom()
                .maxConcurrentCalls(3)
                .build();
        BulkheadRegistry registry = BulkheadRegistry.of(config);
        Bulkhead bulkhead1 = registry.bulkhead("bulkhead1");

        for (int i = 0; i < 30; i++) {
            final int threadNo = i;
            bulkhead1.executeRunnable(() -> {
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
}
