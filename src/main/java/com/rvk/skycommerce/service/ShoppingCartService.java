package com.rvk.skycommerce.service;

import com.rvk.skycommerce.model.ProductType;
import com.rvk.skycommerce.model.ShoppingCartModel;
import com.rvk.skycommerce.repository.entity.CartItem;
import com.rvk.skycommerce.repository.entity.Client;

import java.math.BigDecimal;
import java.util.List;

public interface ShoppingCartService {

    ShoppingCartModel createCartForClient(String clientId);

    ShoppingCartModel getCart(Long cartId);

    ShoppingCartModel addItem(Long cartId, ProductType productType, int quantity);

    ShoppingCartModel updateItemQuantity(Long cartId, Long itemId, int quantity);

    ShoppingCartModel removeItem(Long cartId, Long itemId);

    BigDecimal calculateTotal(Client client, List<CartItem> items);

}