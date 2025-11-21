package com.rvk.skycommerce.model;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class CartItemModel {

    Long id;
    ProductType productType;
    int quantity;
    BigDecimal unitPrice;
    BigDecimal lineTotal;
}