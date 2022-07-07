package io.github.multicatch.resilience4j.circuitbreaker;

import io.github.multicatch.resilience4j.common.BusinessException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.vavr.CheckedRunnable;

import static io.github.multicatch.resilience4j.circuitbreaker.CircuitBreakerTester.runUntilCircuitIsOpen;

public class IgnoredExceptionExample {
    public static void main(String[] args) throws Throwable {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .minimumNumberOfCalls(10)
                .ignoreExceptions(BusinessException.class)
                .build();

        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(circuitBreakerConfig);

        CircuitBreaker breaker1 = registry.circuitBreaker("breaker1");
        CheckedRunnable checkedRunnable = breaker1.decorateCheckedRunnable(() -> {
            throw new BusinessException();
        });

        runUntilCircuitIsOpen(checkedRunnable);

        try {
            checkedRunnable.run();
        } catch (BusinessException e) {
            System.out.println("Wow, it actually throws BusinessException!");
            e.printStackTrace();
        }

    }
}
