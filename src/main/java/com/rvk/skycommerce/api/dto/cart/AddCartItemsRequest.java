package com.rvk.skycommerce.api.dto.cart;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Value;

import java.util.List;

@Value
public class AddCartItemsRequest {


    @NotEmpty
    @Valid
    List<AddCartItem> items;
}
