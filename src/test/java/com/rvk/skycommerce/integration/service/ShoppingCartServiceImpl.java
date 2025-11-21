package com.rvk.skycommerce.integration.service;

import com.rvk.skycommerce.exception.NotFoundException;
import com.rvk.skycommerce.model.ProductType;
import com.rvk.skycommerce.model.ShoppingCartModel;
import com.rvk.skycommerce.repository.ClientRepository;
import com.rvk.skycommerce.repository.ShoppingCartRepository;
import com.rvk.skycommerce.repository.entity.CartItem;
import com.rvk.skycommerce.repository.entity.IndividualClient;
import com.rvk.skycommerce.repository.entity.ProfessionalClient;
import com.rvk.skycommerce.repository.entity.ShoppingCart;
import com.rvk.skycommerce.service.ShoppingCartService;
import com.rvk.skycommerce.service.pricing.PriceCalculator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@Transactional
class ShoppingCartServiceImplIT {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @MockBean
    private PriceCalculator priceCalculator;

    @Test
    void createCartForClient_persistsCartAndReturnsModel_forIndividualClient() {
        IndividualClient client = new IndividualClient("IND-1", "John", "Doe");
        clientRepository.save(client);

        ShoppingCartModel model = shoppingCartService.createCartForClient("IND-1");

        assertThat(model.getId()).isNotNull();
        assertThat(model.getClientId()).isEqualTo("IND-1");
        assertThat(model.getItems()).isEmpty();
        assertThat(model.getTotalAmount()).isEqualByComparingTo(BigDecimal.ZERO);

        ShoppingCart entity = shoppingCartRepository.findById(model.getId())
                .orElseThrow();
        assertThat(entity.getClient().getId()).isEqualTo("IND-1");
    }

    @Test
    void createCartForClient_persistsCartAndReturnsModel_forProfessionalClientLowRevenue() {
        ProfessionalClient client = new ProfessionalClient(
                "PRO-LOW",
                "LowCorp",
                "REG-LOW",
                new BigDecimal("8000000.00"), // < 10M
                "EU-LOW"
        );
        clientRepository.save(client);

        ShoppingCartModel model = shoppingCartService.createCartForClient("PRO-LOW");

        assertThat(model.getId()).isNotNull();
        assertThat(model.getClientId()).isEqualTo("PRO-LOW");
        assertThat(model.getItems()).isEmpty();
        assertThat(model.getTotalAmount()).isEqualByComparingTo(BigDecimal.ZERO);

        ShoppingCart entity = shoppingCartRepository.findById(model.getId())
                .orElseThrow();
        assertThat(entity.getClient().getId()).isEqualTo("PRO-LOW");
    }

    @Test
    void createCartForClient_throwsWhenClientNotFound() {
        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> shoppingCartService.createCartForClient("UNKNOWN")
        );

        assertThat(ex.getMessage()).isEqualTo("Client with id UNKNOWN not found");
    }

    @Test
    void addItem_persistsItemAndCalculatesTotals_forIndividualClient() {
        IndividualClient client = new IndividualClient("IND-2", "Anna", "Smith");
        clientRepository.save(client);

        ShoppingCart cart = new ShoppingCart(client);
        cart.setItems(new ArrayList<>());
        cart.setTotalAmount(BigDecimal.ZERO);
        shoppingCartRepository.save(cart);

        given(priceCalculator.getUnitPrice(any(), eq(ProductType.HIGH_END_PHONE)))
                .willReturn(new BigDecimal("1500.00"));

        ShoppingCartModel model = shoppingCartService.addItem(cart.getId(), ProductType.HIGH_END_PHONE, 2);

        assertThat(model.getId()).isEqualTo(cart.getId());
        assertThat(model.getItems()).hasSize(1);
        assertThat(model.getItems().getFirst().getProductType()).isEqualTo(ProductType.HIGH_END_PHONE);
        assertThat(model.getItems().getFirst().getQuantity()).isEqualTo(2);
        assertThat(model.getItems().getFirst().getUnitPrice()).isEqualByComparingTo("1500.00");
        assertThat(model.getItems().getFirst().getLineTotal()).isEqualByComparingTo("3000.00");
        assertThat(model.getTotalAmount()).isEqualByComparingTo("3000.00");

        ShoppingCart reloaded = shoppingCartRepository.findByIdWithItems(cart.getId())
                .orElseThrow();
        assertThat(reloaded.getItems()).hasSize(1);
        CartItem item = reloaded.getItems().getFirst();
        assertThat(item.getProductType()).isEqualTo(ProductType.HIGH_END_PHONE);
        assertThat(item.getQuantity()).isEqualTo(2);
    }

    @Test
    void updateItemQuantity_updatesQuantityAndRecalculatesTotal_forProfessionalClientHighRevenue() {
        ProfessionalClient client = new ProfessionalClient(
                "PRO-HIGH",
                "HighCorp",
                "REG-HIGH",
                new BigDecimal("20000000.00"), // > 10M
                "EU-HIGH"
        );
        clientRepository.save(client);

        ShoppingCart cart = new ShoppingCart(client);
        CartItem item = new CartItem(ProductType.LAPTOP, 1);
        List<CartItem> items = new ArrayList<>();
        items.add(item);
        cart.setItems(items);
        cart.setTotalAmount(BigDecimal.ZERO);
        shoppingCartRepository.save(cart);

        given(priceCalculator.getUnitPrice(any(), eq(ProductType.LAPTOP)))
                .willReturn(new BigDecimal("900.00")); // условно для high-revenue pro

        ShoppingCartModel model = shoppingCartService.updateItemQuantity(cart.getId(), item.getId(), 3);

        assertThat(model.getItems()).hasSize(1);
        assertThat(model.getItems().getFirst().getQuantity()).isEqualTo(3);
        assertThat(model.getItems().getFirst().getLineTotal()).isEqualByComparingTo("2700.00");
        assertThat(model.getTotalAmount()).isEqualByComparingTo("2700.00");

        ShoppingCart reloaded = shoppingCartRepository.findByIdWithItems(cart.getId())
                .orElseThrow();
        CartItem reloadedItem = reloaded.getItems().getFirst();
        assertThat(reloadedItem.getQuantity()).isEqualTo(3);
    }

    @Test
    void removeItem_removesItemAndRecalculatesTotal_forProfessionalClientLowRevenue() {
        ProfessionalClient client = new ProfessionalClient(
                "PRO-LOW2",
                "LowCorp2",
                "REG-LOW2",
                new BigDecimal("5000000.00"), // < 10M
                "EU-LOW2"
        );
        clientRepository.save(client);

        ShoppingCart cart = new ShoppingCart(client);

        CartItem item1 = new CartItem(ProductType.MID_RANGE_PHONE, 1);
        CartItem item2 = new CartItem(ProductType.LAPTOP, 2);
        List<CartItem> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        cart.setItems(items);
        cart.setTotalAmount(BigDecimal.valueOf(2600.00));
        shoppingCartRepository.save(cart);

        given(priceCalculator.getUnitPrice(any(), eq(ProductType.MID_RANGE_PHONE)))
                .willReturn(new BigDecimal("600.00"));   // pro < 10M
        given(priceCalculator.getUnitPrice(any(), eq(ProductType.LAPTOP)))
                .willReturn(new BigDecimal("1000.00"));  // pro < 10M

        ShoppingCartModel before = shoppingCartService.getCart(cart.getId());
        assertThat(before.getTotalAmount()).isEqualByComparingTo("2600.00"); // 600 + 2*1000

        ShoppingCartModel after = shoppingCartService.removeItem(cart.getId(), item1.getId());

        assertThat(after.getItems()).hasSize(1);
        assertThat(after.getItems().getFirst().getProductType()).isEqualTo(ProductType.LAPTOP);
        assertThat(after.getTotalAmount()).isEqualByComparingTo("2000.00");

        ShoppingCart reloaded = shoppingCartRepository.findByIdWithItems(cart.getId())
                .orElseThrow();
        assertThat(reloaded.getItems()).hasSize(1);
        assertThat(reloaded.getItems().getFirst().getProductType()).isEqualTo(ProductType.LAPTOP);
    }

    @Test
    void addItem_throwsWhenCartNotFound() {
        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> shoppingCartService.addItem(999L, ProductType.LAPTOP, 1)
        );

        assertThat(ex.getMessage()).isEqualTo("Cart with id 999 not found");
    }
}
