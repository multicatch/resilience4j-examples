package io.github.multicatch.resilience4j.circuitbreaker;

import io.github.multicatch.resilience4j.common.BusinessException;
import io.github.multicatch.resilience4j.common.FatalException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.vavr.CheckedRunnable;

import java.time.Duration;

import static io.github.multicatch.resilience4j.circuitbreaker.CircuitBreakerTester.runAndRetry;
import static io.github.multicatch.resilience4j.circuitbreaker.CircuitBreakerTester.runUntilCircuitIsOpen;

public class CustomConfigExample {

    public static void main(String[] args) throws Throwable {
        CircuitBreakerConfig circuitBreakerConfig = createConfig();
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(circuitBreakerConfig);

        CheckedRunnable throwingFatalException = () -> {
            throw new FatalException();
        };

        System.out.println("=== Circuit breaker no 1 ===");
        CircuitBreaker circuitBreakerWithDefaultConfig = registry.circuitBreaker("breaker1");
        CheckedRunnable alwaysError = circuitBreakerWithDefaultConfig.decorateCheckedRunnable(throwingFatalException);

        runUntilCircuitIsOpen(alwaysError);

        System.out.println("=== Circuit breaker no 2 ===");
        CircuitBreaker circuitBreakerWithCustomConfig = registry.circuitBreaker("breaker2",
                CircuitBreakerConfig.custom()
                        .ignoreExceptions(FatalException.class)
                        .build()
        );
        CheckedRunnable customIgnoredError = circuitBreakerWithCustomConfig.decorateCheckedRunnable(throwingFatalException);

        runUntilCircuitIsOpen(customIgnoredError);
    }

    private static CircuitBreakerConfig createConfig() {
        return CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .slowCallRateThreshold(50)
                .waitDurationInOpenState(Duration.ofMillis(1000))
                .slowCallDurationThreshold(Duration.ofSeconds(2))
                .permittedNumberOfCallsInHalfOpenState(3)
                .minimumNumberOfCalls(10)
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.TIME_BASED)
                .slidingWindowSize(5)
                .ignoreExceptions(BusinessException.class)
                .build();

    }
}
