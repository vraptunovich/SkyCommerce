package com.rvk.skycommerce.repository.entity;

import com.rvk.skycommerce.model.ProductType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_type", length = 30, nullable = false)
    private ProductType productType;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    public CartItem(ProductType productType, int quantity) {
        this.productType = productType;
        this.quantity = quantity;
    }
}
