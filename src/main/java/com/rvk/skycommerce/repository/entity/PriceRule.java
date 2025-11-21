package com.rvk.skycommerce.repository.entity;

import com.rvk.skycommerce.model.ClientType;
import com.rvk.skycommerce.model.ProductType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "price_rules")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PriceRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "client_type", length = 20, nullable = false)
    private ClientType clientType;

    @Column(name = "min_revenue_exclusive", precision = 19, scale = 2)
    private BigDecimal minRevenueExclusive;

    @Column(name = "max_revenue_inclusive", precision = 19, scale = 2)
    private BigDecimal maxRevenueInclusive;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_type", length = 30, nullable = false)
    private ProductType productType;

    @Column(name = "price", precision = 19, scale = 2, nullable = false)
    private BigDecimal price;
}
