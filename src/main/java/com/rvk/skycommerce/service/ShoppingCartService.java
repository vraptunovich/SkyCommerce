package com.rvk.skycommerce.service;

import com.rvk.skycommerce.model.ProductType;
import com.rvk.skycommerce.model.ShoppingCartModel;

public interface ShoppingCartService {

    ShoppingCartModel createCartForClient(String clientId);

    ShoppingCartModel getCart(Long cartId);

    ShoppingCartModel addItem(Long cartId, ProductType productType, int quantity);

    ShoppingCartModel updateItemQuantity(Long cartId, Long itemId, int quantity);

    ShoppingCartModel removeItem(Long cartId, Long itemId);


}