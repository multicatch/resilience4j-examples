package io.github.multicatch.resilience4j.ratelimiter;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AtomicRateLimiterExample {
    public static void main(String[] args) {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofSeconds(1))
                .limitForPeriod(2)
                .timeoutDuration(Duration.ofSeconds(25))
                .build();

        RateLimiterRegistry rateLimiterRegistry = RateLimiterRegistry.of(config);

        RateLimiter rateLimiter = rateLimiterRegistry
                .rateLimiter("rateLimiter1");

        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 6; i++) {
            final int threadNo = i;
            executorService.submit(() -> {
                long start = System.currentTimeMillis();
                System.out.printf("Thread #%d: Started.%n", threadNo);
                rateLimiter.acquirePermission();
                long duration = System.currentTimeMillis() - start;
                System.out.printf("Thread #%d: Permission acquired after %d ms.%n", threadNo, duration);
            });
        }

        executorService.shutdown();
    }
}
