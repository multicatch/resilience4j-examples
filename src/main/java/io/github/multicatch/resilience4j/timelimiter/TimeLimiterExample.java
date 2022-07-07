package io.github.multicatch.resilience4j.timelimiter;

import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;

import java.time.Duration;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class TimeLimiterExample {
    public static void main(String[] args) throws Exception {
        TimeLimiterConfig config = TimeLimiterConfig.custom()
                .cancelRunningFuture(true)
                .timeoutDuration(Duration.ofMillis(500))
                .build();

        TimeLimiterRegistry registry = TimeLimiterRegistry.of(config);

        TimeLimiter timeLimiter = registry.timeLimiter("timeLimiter1");

        ExecutorService executorService = Executors.newFixedThreadPool(1);

        try {
            Supplier<? extends Future<String>> fastFunction = () ->
                    executorService.submit(longRunningFunction(200));
            String result = timeLimiter.executeFutureSupplier(fastFunction);
            System.out.println(result);

            Supplier<? extends Future<String>> slowFunction = () ->
                    executorService.submit(longRunningFunction(1000));
            result = timeLimiter.executeFutureSupplier(slowFunction);
            System.out.println(result);
        } finally {
            executorService.shutdown();
        }
    }

    private static Callable<String> longRunningFunction(long millis) {
        return () -> {
            sleep(millis);
            return "result";
        };
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            System.out.println("My sleep has ended.");
        }
    }
}
