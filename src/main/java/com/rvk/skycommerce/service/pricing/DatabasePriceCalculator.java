package com.rvk.skycommerce.service.pricing;

import com.rvk.skycommerce.exception.NotFoundException;
import com.rvk.skycommerce.model.ClientType;
import com.rvk.skycommerce.model.ProductType;
import com.rvk.skycommerce.repository.entity.Client;
import com.rvk.skycommerce.repository.entity.IndividualClient;
import com.rvk.skycommerce.repository.entity.PriceRule;
import com.rvk.skycommerce.repository.entity.ProfessionalClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "skycommerce.pricing.mode", havingValue = "db")
@Slf4j
public class DatabasePriceCalculator implements PriceCalculator {

    private final PriceRuleCacheService priceRuleCacheService;

    @Override
    public BigDecimal getUnitPrice(Client client, ProductType productType) {
        log.debug("getUnitPrice - clientId={} clientType={} productType={}", client.getId(), client.getClass().getSimpleName(), productType);

        ClientType category = resolveCategory(client);
        BigDecimal revenue = resolveRevenue(client);

        PriceRule rule = findRule(category, productType, revenue);
        BigDecimal price = rule.getPrice();

        log.debug("getUnitPrice - found priceRuleId={} price={}", rule.getId(), price);

        return price;
    }

    private PriceRule findRule(ClientType clientType, ProductType productType, BigDecimal annualRevenue) {
        List<PriceRule> allRules = priceRuleCacheService.getAllPriceRules();

        log.debug("findRule - totalRules={} clientType={} productType={} annualRevenue={}",
                allRules.size(), clientType, productType, annualRevenue);

        return allRules.stream()
                .filter(rule -> rule.getClientType() == clientType)
                .filter(rule -> rule.getProductType() == productType)
                .filter(rule -> matchesRevenue(rule, annualRevenue))
                .findFirst()
                .orElseThrow(() -> {
                    String msg = "No price rule for clientType " + clientType +
                            ", product " + productType +
                            ", revenue " + annualRevenue;
                    log.warn("findRule - {}", msg);
                    return new NotFoundException(msg);
                });
    }

    private boolean matchesRevenue(PriceRule rule, BigDecimal annualRevenue) {
        BigDecimal from = rule.getMinRevenueExclusive();
        BigDecimal to = rule.getMaxRevenueInclusive();

        log.trace("matchesRevenue - ruleId={} from={} to={} annualRevenue={}",
                rule.getId(), from, to, annualRevenue);

        if (annualRevenue == null) {
            boolean match = from == null && to == null;
            log.trace("matchesRevenue - annualRevenue is null -> match={}", match);

            return match;
        }

        boolean geFrom = from == null || annualRevenue.compareTo(from) >= 0;
        boolean leTo = to == null || annualRevenue.compareTo(to) <= 0;

        boolean result = geFrom && leTo;

        log.trace("matchesRevenue - geFrom={} leTo={} result={}", geFrom, leTo, result);

        return result;
    }

    private ClientType resolveCategory(Client client) {
        if (client instanceof IndividualClient) {
            log.debug("resolveCategory - resolved INDIVIDUAL for clientId={}", client.getId());
            return ClientType.INDIVIDUAL;
        }
        if (client instanceof ProfessionalClient) {
            log.debug("resolveCategory - resolved PROFESSIONAL for clientId={}", client.getId());
            return ClientType.PROFESSIONAL;
        }
        String typeName = client != null ? client.getClass().getName() : "null";
        log.warn("resolveCategory - unsupported client type: {}", typeName);
        throw new IllegalArgumentException("Unsupported client type: " + typeName);
    }

    private BigDecimal resolveRevenue(Client client) {
        if (client instanceof ProfessionalClient professionalClient) {
            BigDecimal revenue = professionalClient.getAnnualRevenue();
            log.debug("resolveRevenue - professional clientId={} revenue={}", professionalClient.getId(), revenue);
            return revenue;
        }
        log.debug("resolveRevenue - no revenue for clientId={}", client != null ? client.getId() : "null");
        return null;
    }
}
