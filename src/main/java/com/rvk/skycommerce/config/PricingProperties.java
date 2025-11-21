package com.rvk.skycommerce.config;

import com.rvk.skycommerce.model.ProductType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.util.Map;

@ConfigurationProperties(prefix = "pricing")
@Data
public class PricingProperties {

    private Map<ProductType, BigDecimal> individual;
    private Professional professional;

    @Data
    public static class Professional {
        private Tier lowRevenue;
        private Tier highRevenue;
    }

    @Data
    public static class Tier {
        private BigDecimal maxRevenueInclusive;
        private BigDecimal minRevenueExclusive;
        private Map<ProductType, BigDecimal> products;
    }
}
