package io.github.multicatch.resilience4j.retry;

import io.github.multicatch.resilience4j.common.BusinessException;
import io.github.multicatch.resilience4j.common.FatalException;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

public class PartialFailExample {
    public static void main(String[] args) throws Throwable {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(5)
                .intervalFunction(IntervalFunction.of(Duration.ofSeconds(1)))
                .ignoreExceptions(BusinessException.class)
                .failAfterMaxAttempts(true)
                .build();

        RetryRegistry registry = RetryRegistry.of(config);
        Retry retry = registry.retry("retry1");

        AtomicInteger trial = new AtomicInteger(1);

        String result = retry.executeCheckedSupplier(() -> {
            int i = trial.getAndIncrement();
            if (i <= 3) {
                System.out.printf("Trial number #%d.%n", i);
                throw new FatalException();
            } else {
                return "success";
            }
        });

        System.out.println("Result: " + result);
    }
}
