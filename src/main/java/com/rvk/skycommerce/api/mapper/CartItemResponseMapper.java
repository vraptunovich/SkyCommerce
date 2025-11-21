package com.rvk.skycommerce.api.mapper;

import com.rvk.skycommerce.api.dto.cart.CartItemResponse;
import com.rvk.skycommerce.api.dto.cart.CartResponse;
import com.rvk.skycommerce.model.CartItemModel;
import com.rvk.skycommerce.model.ShoppingCartModel;

import java.util.List;

public class CartItemResponseMapper {

    public static CartResponse fromModel(ShoppingCartModel model) {
        List<CartItemResponse> itemResponses = model.getItems().stream()
                .map(CartItemResponseMapper::toItemResponse)
                .toList();

        return CartResponse.builder()
                .id(model.getId())
                .clientId(model.getClientId())
                .totalAmount(model.getTotalAmount())
                .items(itemResponses)
                .build();
    }

    private static CartItemResponse toItemResponse(CartItemModel item) {
        return CartItemResponse.builder()
                .id(item.getId())
                .productType(item.getProductType())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .lineTotal(item.getLineTotal())
                .build();
    }
}
