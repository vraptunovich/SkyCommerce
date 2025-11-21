package com.rvk.skycommerce.integration.repository;

import com.rvk.skycommerce.model.ClientType;
import com.rvk.skycommerce.model.ProductType;
import com.rvk.skycommerce.repository.PriceRuleRepository;
import com.rvk.skycommerce.repository.entity.PriceRule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PriceRuleRepositoryTest {

    @Autowired
    private PriceRuleRepository priceRuleRepository;

    @Test
    void shouldFindIndividualPriceForHighEndPhone() {
        Optional<PriceRule> rule = priceRuleRepository.findMatchingRule(
                ClientType.INDIVIDUAL,
                ProductType.HIGH_END_PHONE,
                null
        );

        assertThat(rule).isPresent();
        assertThat(rule.get().getPrice()).isEqualByComparingTo("1500.00");
    }

    @Test
    void shouldFindProfessionalLowRevenuePrice() {
        Optional<PriceRule> rule = priceRuleRepository.findMatchingRule(
                ClientType.PROFESSIONAL,
                ProductType.MID_RANGE_PHONE,
                new BigDecimal("5000000.00")
        );

        assertThat(rule).isPresent();
        assertThat(rule.get().getPrice()).isEqualByComparingTo("600.00");
    }

    @Test
    void shouldUseLowRevenueRuleForExactlyTenMillion() {
        Optional<PriceRule> rule = priceRuleRepository.findMatchingRule(
                ClientType.PROFESSIONAL,
                ProductType.LAPTOP,
                new BigDecimal("10000000.00")
        );

        assertThat(rule).isPresent();
        assertThat(rule.get().getPrice()).isEqualByComparingTo("1000.00");
    }

    @Test
    void shouldFindProfessionalHighRevenuePrice() {
        Optional<PriceRule> rule = priceRuleRepository.findMatchingRule(
                ClientType.PROFESSIONAL,
                ProductType.HIGH_END_PHONE,
                new BigDecimal("15000000.00")
        );

        assertThat(rule).isPresent();
        assertThat(rule.get().getPrice()).isEqualByComparingTo("1000.00");
    }
}
