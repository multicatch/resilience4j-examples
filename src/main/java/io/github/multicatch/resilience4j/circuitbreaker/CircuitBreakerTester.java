package io.github.multicatch.resilience4j.circuitbreaker;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.vavr.CheckedRunnable;

import java.time.Duration;

public class CircuitBreakerTester {

    public static void runAndRetry(CheckedRunnable checkedRunnable, Duration sleepTime, int maxTries) throws Throwable {
        for (int i = 0; i < maxTries; i++) {
            if (!runUntilCircuitIsOpen(checkedRunnable)) {
                break;
            }

            long millis = sleepTime.toMillis();
            System.out.printf("Pausing for %s ms.%n", millis);
            Thread.sleep(millis);
        }
    }

    public static boolean runUntilCircuitIsOpen(CheckedRunnable checkedRunnable) throws Throwable {
        int i = 0;
        long start = System.currentTimeMillis();
        boolean open = false;

        for (; i < 100; i++) {
            try {
                checkedRunnable.run();
            } catch (CallNotPermittedException e) {
                open = true;
                e.printStackTrace();
                break;
            } catch (Exception e) {
                // do nothing and retry
            }
        }
        long duration = System.currentTimeMillis() - start;

        if (open) {
            System.out.printf("Circuit open after %d tries (%d ms).%n", i, duration);
        } else {
            System.out.printf("Circuit still closed after 100 tries (%s ms)%n", duration);
        }

        return open;
    }
}
