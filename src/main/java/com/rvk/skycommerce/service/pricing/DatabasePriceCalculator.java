package com.rvk.skycommerce.service.pricing;

import com.rvk.skycommerce.exception.NotFoundException;
import com.rvk.skycommerce.model.ClientType;
import com.rvk.skycommerce.model.ProductType;
import com.rvk.skycommerce.repository.entity.Client;
import com.rvk.skycommerce.repository.entity.IndividualClient;
import com.rvk.skycommerce.repository.entity.PriceRule;
import com.rvk.skycommerce.repository.entity.ProfessionalClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "skycommerce.pricing.mode", havingValue = "db")
public class DatabasePriceCalculator implements PriceCalculator {

    private final PriceRuleCacheService priceRuleCacheService;

    @Override
    public BigDecimal getUnitPrice(Client client, ProductType productType) {
        ClientType category = resolveCategory(client);
        BigDecimal revenue = resolveRevenue(client);
        PriceRule rule = findRule(category, productType, revenue);
        return rule.getPrice();
    }

    private PriceRule findRule(ClientType clientType, ProductType productType, BigDecimal annualRevenue) {
        return priceRuleCacheService.getAllPriceRules().stream()
                .filter(rule -> rule.getClientType() == clientType)
                .filter(rule -> rule.getProductType() == productType)
                .filter(rule -> matchesRevenue(rule, annualRevenue))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(
                        "No price rule for clientType " + clientType +
                                ", product " + productType +
                                ", revenue " + annualRevenue
                ));
    }

    private boolean matchesRevenue(PriceRule rule, BigDecimal annualRevenue) {
        BigDecimal from = rule.getMinRevenueExclusive();
        BigDecimal to = rule.getMaxRevenueInclusive();
        if (annualRevenue == null) {
            return from == null && to == null;
        }

        boolean geFrom = from == null || annualRevenue.compareTo(from) >= 0;
        boolean leTo = to == null || annualRevenue.compareTo(to) <= 0;

        return geFrom && leTo;
    }

    private ClientType resolveCategory(Client client) {
        if (client instanceof IndividualClient) {
            return ClientType.INDIVIDUAL;
        }
        if (client instanceof ProfessionalClient) {
            return ClientType.PROFESSIONAL;
        }
        throw new IllegalArgumentException("Unsupported client type: " + client.getClass().getName());
    }

    private BigDecimal resolveRevenue(Client client) {
        if (client instanceof ProfessionalClient professionalClient) {
            return professionalClient.getAnnualRevenue();
        }
        return null;
    }
}
