package com.rvk.skycommerce.api.dto.cart;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

@Value
@Builder
public class CartResponse {

    Long id;
    String clientId;
    BigDecimal totalAmount;
    List<CartItemResponse> items;


}

