package com.rvk.skycommerce.integration.repository;

import com.rvk.skycommerce.model.ProductType;
import com.rvk.skycommerce.repository.CartItemRepository;
import com.rvk.skycommerce.repository.ClientRepository;
import com.rvk.skycommerce.repository.ShoppingCartRepository;
import com.rvk.skycommerce.repository.entity.CartItem;
import com.rvk.skycommerce.repository.entity.Client;
import com.rvk.skycommerce.repository.entity.ShoppingCart;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CartItemRepositoryTest {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ShoppingCartRepository cartRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Test
    void shouldSaveCartItem() {
        Client client = clientRepository.findById("C_IND_002").orElseThrow();
        ShoppingCart cart = new ShoppingCart(client);
        cart.setTotalAmount(BigDecimal.ZERO);
        cart.getItems().add(new CartItem(ProductType.MID_RANGE_PHONE, 3));
        ShoppingCart savedCart = cartRepository.save(cart);

        assertThat(savedCart.getItems()).hasSize(1);
        CartItem item = savedCart.getItems().get(0);
        assertThat(item.getId()).isNotNull();

        CartItem loaded = cartItemRepository.findById(item.getId()).orElseThrow();
        assertThat(loaded.getProductType()).isEqualTo(ProductType.MID_RANGE_PHONE);
        assertThat(loaded.getQuantity()).isEqualTo(3);
    }
}
