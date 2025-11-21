package com.rvk.skycommerce.service.pricing;

import com.rvk.skycommerce.repository.PriceRuleRepository;
import com.rvk.skycommerce.repository.entity.PriceRule;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceRuleCacheService {

    private final PriceRuleRepository priceRuleRepository;

    @Cacheable("priceRules")
    public List<PriceRule> getAllPriceRules() {
        return priceRuleRepository.findAll();
    }
}

