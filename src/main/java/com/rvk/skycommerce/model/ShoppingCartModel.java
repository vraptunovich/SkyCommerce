package com.rvk.skycommerce.model;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

@Value
@Builder
public class ShoppingCartModel {

    Long id;
    String clientId;
    List<CartItemModel> items;
    BigDecimal totalAmount;
}
