package com.rvk.skycommerce.repository;

import com.rvk.skycommerce.model.ClientType;
import com.rvk.skycommerce.model.ProductType;
import com.rvk.skycommerce.repository.entity.PriceRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface PriceRuleRepository extends JpaRepository<PriceRule, Long> {

    @Query("""
            select r from PriceRule r
            where r.clientType = :clientType
              and r.productType = :productType
              and (
                   :annualRevenue is null
                   or (
                        (r.minRevenueExclusive is null or :annualRevenue > r.minRevenueExclusive)
                    and (r.maxRevenueInclusive is null or :annualRevenue <= r.maxRevenueInclusive)
                   )
              )
            """)
    Optional<PriceRule> findMatchingRule(
            @Param("clientType") ClientType clientType,
            @Param("productType") ProductType productType,
            @Param("annualRevenue") BigDecimal annualRevenue
    );
}
