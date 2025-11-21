package com.rvk.skycommerce.api.dto.cart;

import jakarta.validation.constraints.Min;
import lombok.Value;

@Value
public class UpdateCartItemQuantityRequest {

    @Min(1)
    int quantity;
}
