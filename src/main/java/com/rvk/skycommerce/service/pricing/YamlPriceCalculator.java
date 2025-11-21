package com.rvk.skycommerce.service.pricing;

import com.rvk.skycommerce.config.PricingProperties;
import com.rvk.skycommerce.model.ProductType;
import com.rvk.skycommerce.repository.entity.Client;
import com.rvk.skycommerce.repository.entity.IndividualClient;
import com.rvk.skycommerce.repository.entity.ProfessionalClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
@ConditionalOnProperty(name = "skycommerce.pricing.mode", havingValue = "yaml", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class YamlPriceCalculator implements PriceCalculator {

    private final PricingProperties pricingProperties;

    @Override
    public BigDecimal getUnitPrice(Client client, ProductType productType) {
        log.debug("getUnitPrice - clientId={} clientType={} productType={}", client.getId(), client.getClass().getSimpleName(), productType);

        if (client instanceof IndividualClient) {
            BigDecimal price = getIndividualPrice(productType);
            log.debug("getUnitPrice - individual price for productType={} = {}", productType, price);
            return price;
        }
        if (client instanceof ProfessionalClient professionalClient) {
            BigDecimal price = getProfessionalPrice(professionalClient.getAnnualRevenue(), productType);
            log.debug("getUnitPrice - professional price for revenue={} productType={} = {}",
                    professionalClient.getAnnualRevenue(), productType, price);
            return price;
        }

        log.error("getUnitPrice - unsupported client type: {}", client.getClass().getName());
        throw new IllegalArgumentException("Unsupported client type: " + client.getClass().getName());
    }

    private BigDecimal getIndividualPrice(ProductType productType) {
        log.debug("getIndividualPrice - productType={}", productType);

        Map<ProductType, BigDecimal> map = pricingProperties.getIndividual();

        if (map == null || !map.containsKey(productType)) {
            log.warn("getIndividualPrice - no individual price configured for product {}", productType);

            throw new IllegalStateException("No individual price configured for product " + productType);
        }
        BigDecimal price = map.get(productType);

        log.trace("getIndividualPrice - resolved price={}", price);

        return price;
    }

    private BigDecimal getProfessionalPrice(BigDecimal revenue, ProductType productType) {
        log.debug("getProfessionalPrice - revenue={} productType={}", revenue, productType);

        PricingProperties.Professional professional = pricingProperties.getProfessional();
        if (professional == null) {
            log.warn("getProfessionalPrice - no professional pricing configured");
            throw new IllegalStateException("No professional pricing configured");
        }

        PricingProperties.Tier lowTier = professional.getLowRevenue();
        PricingProperties.Tier highTier = professional.getHighRevenue();

        PricingProperties.Tier tier = selectTier(revenue, lowTier, highTier);
        String tierName = tier == lowTier ? "lowRevenue" : tier == highTier ? "highRevenue" : "unknown";
        if (tier == null || tier.getProducts() == null || !tier.getProducts().containsKey(productType)) {
            log.warn("getProfessionalPrice - no professional price configured for product {} and revenue {} (selectedTier={})",
                    productType, revenue, tierName);
            throw new IllegalStateException("No professional price configured for product " + productType + " and revenue " + revenue);
        }
        BigDecimal price = tier.getProducts().get(productType);

        log.debug("getProfessionalPrice - selectedTier={} price={}", tierName, price);

        return price;
    }

    private PricingProperties.Tier selectTier(BigDecimal revenue,
                                              PricingProperties.Tier lowTier,
                                              PricingProperties.Tier highTier) {
        log.debug("selectTier - revenue={} lowTierMinMax={} highTierMinExclusive={}",
                revenue,
                lowTier != null ? lowTier.getMinRevenueExclusive() + "/" + lowTier.getMaxRevenueInclusive() : "null",
                highTier != null ? highTier.getMinRevenueExclusive() : "null");

        if (revenue == null) {
            log.debug("selectTier - revenue is null, choosing lowTier");
            return lowTier;
        }

        BigDecimal highMinExclusive = highTier != null ? highTier.getMinRevenueExclusive() : null;
        BigDecimal lowMaxInclusive = lowTier != null ? lowTier.getMaxRevenueInclusive() : null;

        if (highMinExclusive != null && revenue.compareTo(highMinExclusive) > 0) {
            log.debug("selectTier - revenue {} > highMinExclusive {}, choosing highTier", revenue, highMinExclusive);
            return highTier;
        }

        if (lowMaxInclusive != null && revenue.compareTo(lowMaxInclusive) <= 0) {
            log.debug("selectTier - revenue {} <= lowMaxInclusive {}, choosing lowTier", revenue, lowMaxInclusive);
            return lowTier;
        }

        log.debug("selectTier - defaulting to lowTier");
        return lowTier;
    }
}