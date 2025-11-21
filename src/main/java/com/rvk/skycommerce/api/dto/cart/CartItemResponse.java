package com.rvk.skycommerce.api.dto.cart;

import com.rvk.skycommerce.model.ProductType;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class CartItemResponse {

    Long id;
    ProductType productType;
    int quantity;
    BigDecimal unitPrice;
    BigDecimal lineTotal;
}