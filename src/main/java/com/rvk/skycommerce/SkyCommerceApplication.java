package com.rvk.skycommerce;

import com.rvk.skycommerce.config.CacheProperties;
import com.rvk.skycommerce.config.PricingProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({PricingProperties.class, CacheProperties.class})
public class SkyCommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkyCommerceApplication.class, args);
    }

}
