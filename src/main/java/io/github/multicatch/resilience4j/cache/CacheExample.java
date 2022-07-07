package io.github.multicatch.resilience4j.cache;

import io.github.resilience4j.cache.Cache;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.AccessedExpiryPolicy;
import javax.cache.expiry.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class CacheExample {
    public static void main(String[] args) throws InterruptedException {
        CacheManager cacheManager = Caching.getCachingProvider().getCacheManager();

        MutableConfiguration<String, String> configuration = new MutableConfiguration<String, String>()
                        .setTypes(String.class, String.class)
                        .setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(
                                new Duration(TimeUnit.SECONDS, 2L)
                        ));

        Cache<String, String> cache = Cache.of(cacheManager
                .createCache("cacheName", configuration));

        Function<String, String> decoratedSupplier = Cache.decorateSupplier(cache, () -> {
            System.out.println("Actually called this time!");
            return "result";
        });

        for (int i = 1; i <= 10; i++) {
            System.out.println("Call #" + i);
            Object result = decoratedSupplier.apply("key");
            System.out.println(result);
        }

        System.out.println("Waiting for cache to expire...");
        Thread.sleep(2000);

        decoratedSupplier.apply("key");
    }
}
