package io.github.multicatch.resilience4j.retry;

import io.github.multicatch.resilience4j.common.BusinessException;
import io.github.multicatch.resilience4j.common.FatalException;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class AlwaysFailExample {
    public static void main(String[] args) {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(5)
                .intervalFunction(IntervalFunction.ofRandomized(Duration.ofMillis(1000), 0.5))
                .ignoreExceptions(BusinessException.class)
                .failAfterMaxAttempts(true)
                .build();

        RetryRegistry registry = RetryRegistry.of(config);

        Retry retry = registry.retry("name1");

        AtomicInteger number = new AtomicInteger(1);
        AtomicLong atomicLong = new AtomicLong(System.currentTimeMillis());

        retry.executeRunnable(() -> {
            long interval = System.currentTimeMillis() - atomicLong.getAndSet(System.currentTimeMillis());
            System.out.printf("Trial number #%d, after %d ms%n", number.getAndIncrement(), interval);
            throw new FatalException();
        });
    }
}
