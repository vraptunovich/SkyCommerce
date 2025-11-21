package com.rvk.skycommerce.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Data
@ConfigurationProperties(prefix = "skycommerce.cache")
public class CacheProperties {

    private CacheSpec shoppingCarts;
    private CacheSpec priceRules;

    @Data
    public static class CacheSpec {
        private Duration ttl;
        private long maxSize;
    }
}
