package io.github.multicatch.resilience4j.circuitbreaker;

import io.github.multicatch.resilience4j.common.BusinessException;
import io.github.multicatch.resilience4j.common.FatalException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.vavr.CheckedRunnable;

import java.time.Duration;

import static io.github.multicatch.resilience4j.circuitbreaker.CircuitBreakerTester.runAndRetry;

public class HalfOpenExample {
    public static void main(String[] args) throws Throwable {
        CircuitBreakerConfig circuitBreakerConfig =  CircuitBreakerConfig.custom()
                .minimumNumberOfCalls(10)
                .waitDurationInOpenState(Duration.ofMillis(500))
                .permittedNumberOfCallsInHalfOpenState(8)
                .ignoreExceptions(BusinessException.class)
                .build();
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(circuitBreakerConfig);

        CheckedRunnable runnable = registry.circuitBreaker("breaker1")
                .decorateCheckedRunnable(() -> {
                    throw new FatalException();
                });

        runAndRetry(runnable, Duration.ofMillis(500), 4);
    }
}
