package io.github.multicatch.resilience4j.retry;

import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class FailOnInvalidResultExample {
    public static void main(String[] args) {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(5)
                .intervalFunction(IntervalFunction.of(Duration.ofMillis(1000)))
                .retryOnResult(Objects::isNull)
                .failAfterMaxAttempts(false)
                .build();

        RetryRegistry registry = RetryRegistry.of(config);
        Retry retry = registry.retry("retry1");

        retry.getEventPublisher()
                .onError(e -> System.out.println("Oopsie!"));

        AtomicInteger trial = new AtomicInteger(1);
        Object result = retry.executeSupplier(() -> {
            System.out.printf("Trial number #%d.%n", trial.getAndIncrement());
            return null;
        });

        System.out.println(result);
    }
}
