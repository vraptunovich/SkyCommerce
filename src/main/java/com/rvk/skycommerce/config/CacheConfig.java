package com.rvk.skycommerce.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
@EnableConfigurationProperties(CacheProperties.class)
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(CacheProperties properties) {
        CacheProperties.CacheSpec shopping = properties.getShoppingCarts();
        CacheProperties.CacheSpec priceRules = properties.getShoppingCarts();

        CaffeineCache shoppingCartsCache = new CaffeineCache(
                "shoppingCarts",
                Caffeine.newBuilder()
                        .expireAfterWrite(shopping.getTtl().toMinutes(), TimeUnit.MINUTES)
                        .maximumSize(shopping.getMaxSize())
                        .build()
        );

        CaffeineCache priceRulesCache = new CaffeineCache(
                "priceRules",
                Caffeine.newBuilder()
                        .expireAfterWrite(priceRules.getTtl().toMinutes(), TimeUnit.MINUTES)
                        .maximumSize(priceRules.getMaxSize())
                        .build()
        );


        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(List.of(shoppingCartsCache, priceRulesCache));
        return manager;
    }
}
