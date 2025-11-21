package com.rvk.skycommerce.api.dto.cart;

import com.rvk.skycommerce.model.ProductType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class AddCartItem {

    @NotNull
    ProductType productType;

    @Min(1)
    int quantity;
}
