package com.rvk.skycommerce.service.pricing;

import com.rvk.skycommerce.config.PricingProperties;
import com.rvk.skycommerce.model.ProductType;
import com.rvk.skycommerce.repository.entity.Client;
import com.rvk.skycommerce.repository.entity.IndividualClient;
import com.rvk.skycommerce.repository.entity.ProfessionalClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
@ConditionalOnProperty(name = "skycommerce.pricing.mode", havingValue = "yaml", matchIfMissing = true)
@RequiredArgsConstructor
public class YamlPriceCalculator implements PriceCalculator {

    private final PricingProperties pricingProperties;

    @Override
    public BigDecimal getUnitPrice(Client client, ProductType productType) {
        if (client instanceof IndividualClient) {
            return getIndividualPrice(productType);
        }
        if (client instanceof ProfessionalClient professionalClient) {
            return getProfessionalPrice(professionalClient.getAnnualRevenue(), productType);
        }
        throw new IllegalArgumentException("Unsupported client type: " + client.getClass().getName());
    }

    private BigDecimal getIndividualPrice(ProductType productType) {
        Map<ProductType, BigDecimal> map = pricingProperties.getIndividual();
        if (map == null || !map.containsKey(productType)) {
            throw new IllegalStateException("No individual price configured for product " + productType);
        }
        return map.get(productType);
    }

    private BigDecimal getProfessionalPrice(BigDecimal revenue, ProductType productType) {
        PricingProperties.Professional professional = pricingProperties.getProfessional();
        if (professional == null) {
            throw new IllegalStateException("No professional pricing configured");
        }

        PricingProperties.Tier lowTier = professional.getLowRevenue();
        PricingProperties.Tier highTier = professional.getHighRevenue();

        PricingProperties.Tier tier = selectTier(revenue, lowTier, highTier);
        if (tier == null || tier.getProducts() == null || !tier.getProducts().containsKey(productType)) {
            throw new IllegalStateException("No professional price configured for product " + productType + " and revenue " + revenue);
        }
        return tier.getProducts().get(productType);
    }

    private PricingProperties.Tier selectTier(BigDecimal revenue,
                                              PricingProperties.Tier lowTier,
                                              PricingProperties.Tier highTier) {
        if (revenue == null) {
            return lowTier;
        }

        BigDecimal highMinExclusive = highTier != null ? highTier.getMinRevenueExclusive() : null;
        BigDecimal lowMaxInclusive = lowTier != null ? lowTier.getMaxRevenueInclusive() : null;

        if (highMinExclusive != null && revenue.compareTo(highMinExclusive) > 0) {
            return highTier;
        }

        if (lowMaxInclusive != null && revenue.compareTo(lowMaxInclusive) <= 0) {
            return lowTier;
        }

        return lowTier;
    }
}