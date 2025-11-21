package com.rvk.skycommerce.integration.repository;

import com.rvk.skycommerce.model.ProductType;
import com.rvk.skycommerce.repository.ClientRepository;
import com.rvk.skycommerce.repository.ShoppingCartRepository;
import com.rvk.skycommerce.repository.entity.CartItem;
import com.rvk.skycommerce.repository.entity.Client;
import com.rvk.skycommerce.repository.entity.ShoppingCart;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ShoppingCartRepositoryTest {

    @Autowired
    private ShoppingCartRepository cartRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Test
    void shouldCreateCartForExistingClientAndLoadByClientId() {
        Client client = clientRepository.findById("C_IND_001").orElseThrow();
        ShoppingCart cart = new ShoppingCart(client);
        cart.getItems().add(new CartItem(ProductType.HIGH_END_PHONE, 2));
        cart.getItems().add(new CartItem(ProductType.LAPTOP, 1));
        cart.setTotalAmount(BigDecimal.ZERO);
        ShoppingCart saved = cartRepository.save(cart);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getItems()).hasSize(2);

        Optional<ShoppingCart> byClient = cartRepository.findByClient_Id("C_IND_001");
        assertThat(byClient).isPresent();
        ShoppingCart loaded = byClient.get();
        assertThat(loaded.getId()).isEqualTo(saved.getId());
        assertThat(loaded.getItems()).hasSize(2);
        assertThat(loaded.getItems())
                .extracting("productType")
                .containsExactlyInAnyOrder(ProductType.HIGH_END_PHONE, ProductType.LAPTOP);
    }

    @Test
    void shouldReturnEmptyWhenCartForClientDoesNotExist() {
        Optional<ShoppingCart> byClient = cartRepository.findByClient_Id("NON_EXISTING");
        assertThat(byClient).isEmpty();
    }
}
