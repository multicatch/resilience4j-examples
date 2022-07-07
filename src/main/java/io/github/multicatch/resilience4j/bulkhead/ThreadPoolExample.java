package io.github.multicatch.resilience4j.bulkhead;

import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.bulkhead.ThreadPoolBulkhead;
import io.github.resilience4j.bulkhead.ThreadPoolBulkheadConfig;
import io.github.resilience4j.bulkhead.ThreadPoolBulkheadRegistry;

public class ThreadPoolExample {
    public static void main(String[] args) {
        ThreadPoolBulkheadConfig config = ThreadPoolBulkheadConfig.custom()
                .coreThreadPoolSize(3)
                .maxThreadPoolSize(3)
                .build();

        ThreadPoolBulkheadRegistry registry = ThreadPoolBulkheadRegistry.of(config);
        ThreadPoolBulkhead bulkhead1 = registry.bulkhead("bulkhead1");

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
