package com.rvk.skycommerce.mock.service;

import com.rvk.skycommerce.exception.NotFoundException;
import com.rvk.skycommerce.model.CartItemModel;
import com.rvk.skycommerce.model.ProductType;
import com.rvk.skycommerce.model.ShoppingCartModel;
import com.rvk.skycommerce.repository.ClientRepository;
import com.rvk.skycommerce.repository.ShoppingCartRepository;
import com.rvk.skycommerce.repository.entity.CartItem;
import com.rvk.skycommerce.repository.entity.IndividualClient;
import com.rvk.skycommerce.repository.entity.ShoppingCart;
import com.rvk.skycommerce.service.ShoppingCartServiceImpl;
import com.rvk.skycommerce.service.pricing.PriceCalculator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceImplTest {

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private PriceCalculator priceCalculator;

    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    @Test
    void createCartForClient_shouldCreateCartAndReturnModel() {
        IndividualClient client = new IndividualClient("CLIENT-1");

        when(clientRepository.findById("CLIENT-1")).thenReturn(Optional.of(client));

        ShoppingCart cart = new ShoppingCart(client);
        cart.setId(10L);
        cart.setItems(new ArrayList<>());

        when(shoppingCartRepository.save(any(ShoppingCart.class)))
                .thenReturn(cart);

        ShoppingCartModel result = shoppingCartService.createCartForClient("CLIENT-1");

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getClientId()).isEqualTo("CLIENT-1");
        assertThat(result.getItems()).isEmpty();
        assertThat(result.getTotalAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void createCartForClient_shouldThrowWhenClientNotFound() {
        when(clientRepository.findById("UNKNOWN")).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> shoppingCartService.createCartForClient("UNKNOWN")
        );

        assertThat(ex.getMessage()).isEqualTo("Client with id UNKNOWN not found");
    }

    @Test
    void getCart_shouldReturnCartModelWithCalculatedTotals() {
        IndividualClient client = new IndividualClient("CLIENT-1");

        ShoppingCart cart = new ShoppingCart(client);
        cart.setId(5L);

        CartItem item1 = new CartItem(ProductType.HIGH_END_PHONE, 2);
        item1.setId(100L);
        CartItem item2 = new CartItem(ProductType.LAPTOP, 1);
        item2.setId(101L);

        cart.setItems(new ArrayList<>(List.of(item1, item2)));

        when(shoppingCartRepository.findByIdWithItems(5L)).thenReturn(Optional.of(cart));

        when(priceCalculator.getUnitPrice(client, ProductType.HIGH_END_PHONE))
                .thenReturn(new BigDecimal("1500.00"));
        when(priceCalculator.getUnitPrice(client, ProductType.LAPTOP))
                .thenReturn(new BigDecimal("1200.00"));

        ShoppingCartModel result = shoppingCartService.getCart(5L);

        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getClientId()).isEqualTo("CLIENT-1");
        assertThat(result.getItems()).hasSize(2);

        CartItemModel m1 = result.getItems().stream()
                .filter(i -> i.getId().equals(100L))
                .findFirst()
                .orElseThrow();
        assertThat(m1.getProductType()).isEqualTo(ProductType.HIGH_END_PHONE);
        assertThat(m1.getQuantity()).isEqualTo(2);
        assertThat(m1.getUnitPrice()).isEqualByComparingTo("1500.00");
        assertThat(m1.getLineTotal()).isEqualByComparingTo("3000.00");

        CartItemModel m2 = result.getItems().stream()
                .filter(i -> i.getId().equals(101L))
                .findFirst()
                .orElseThrow();
        assertThat(m2.getProductType()).isEqualTo(ProductType.LAPTOP);
        assertThat(m2.getQuantity()).isEqualTo(1);
        assertThat(m2.getUnitPrice()).isEqualByComparingTo("1200.00");
        assertThat(m2.getLineTotal()).isEqualByComparingTo("1200.00");

        assertThat(result.getTotalAmount()).isEqualByComparingTo("4200.00");
    }

    @Test
    void getCart_shouldThrowWhenCartNotFound() {
        when(shoppingCartRepository.findByIdWithItems(999L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> shoppingCartService.getCart(999L)
        );

        assertThat(ex.getMessage()).isEqualTo("Cart with id 999 not found");
    }

    @Test
    void addItem_shouldAddNewItemWhenProductNotInCart() {
        IndividualClient client = new IndividualClient("CLIENT-1");

        ShoppingCart cart = new ShoppingCart(client);
        cart.setId(3L);
        cart.setItems(new ArrayList<>());

        when(shoppingCartRepository.findByIdWithItems(3L)).thenReturn(Optional.of(cart));
        when(shoppingCartRepository.save(any(ShoppingCart.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(priceCalculator.getUnitPrice(client, ProductType.MID_RANGE_PHONE))
                .thenReturn(new BigDecimal("800.00"));

        ShoppingCartModel result = shoppingCartService.addItem(3L, ProductType.MID_RANGE_PHONE, 2);

        assertThat(cart.getItems()).hasSize(1);
        CartItem item = cart.getItems().getFirst();
        assertThat(item.getProductType()).isEqualTo(ProductType.MID_RANGE_PHONE);
        assertThat(item.getQuantity()).isEqualTo(2);

        assertThat(result.getItems()).hasSize(1);
        CartItemModel model = result.getItems().getFirst();
        assertThat(model.getProductType()).isEqualTo(ProductType.MID_RANGE_PHONE);
        assertThat(model.getQuantity()).isEqualTo(2);
        assertThat(model.getUnitPrice()).isEqualByComparingTo("800.00");
        assertThat(model.getLineTotal()).isEqualByComparingTo("1600.00");
        assertThat(result.getTotalAmount()).isEqualByComparingTo("1600.00");
    }

    @Test
    void addItem_shouldIncreaseQuantityWhenProductAlreadyInCart() {
        IndividualClient client = new IndividualClient("CLIENT-1");

        ShoppingCart cart = new ShoppingCart(client);
        cart.setId(4L);

        CartItem existing = new CartItem(ProductType.LAPTOP, 1);
        existing.setId(200L);
        cart.setItems(new ArrayList<>(List.of(existing)));

        when(shoppingCartRepository.findByIdWithItems(4L)).thenReturn(Optional.of(cart));
        when(shoppingCartRepository.save(any(ShoppingCart.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(priceCalculator.getUnitPrice(client, ProductType.LAPTOP))
                .thenReturn(new BigDecimal("1200.00"));

        ShoppingCartModel result = shoppingCartService.addItem(4L, ProductType.LAPTOP, 2);

        assertThat(existing.getQuantity()).isEqualTo(3);

        CartItemModel itemModel = result.getItems().getFirst();
        assertThat(itemModel.getQuantity()).isEqualTo(3);
        assertThat(itemModel.getLineTotal()).isEqualByComparingTo("3600.00");
        assertThat(result.getTotalAmount()).isEqualByComparingTo("3600.00");
    }

    @Test
    void addItem_shouldThrowWhenQuantityNonPositive() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> shoppingCartService.addItem(1L, ProductType.LAPTOP, 0)
        );

        assertThat(ex.getMessage()).isEqualTo("Quantity must be positive");
    }

    @Test
    void addItem_shouldThrowWhenCartNotFound() {
        when(shoppingCartRepository.findByIdWithItems(10L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> shoppingCartService.addItem(10L, ProductType.LAPTOP, 1)
        );

        assertThat(ex.getMessage()).isEqualTo("Cart with id 10 not found");
    }

    @Test
    void updateItemQuantity_shouldUpdateExistingItem() {
        IndividualClient client = new IndividualClient("CLIENT-1");

        ShoppingCart cart = new ShoppingCart(client);
        cart.setId(7L);

        CartItem item = new CartItem(ProductType.HIGH_END_PHONE, 1);
        item.setId(300L);
        cart.setItems(new ArrayList<>(List.of(item)));

        when(shoppingCartRepository.findByIdWithItems(7L)).thenReturn(Optional.of(cart));
        when(shoppingCartRepository.save(any(ShoppingCart.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(priceCalculator.getUnitPrice(client, ProductType.HIGH_END_PHONE))
                .thenReturn(new BigDecimal("1500.00"));

        ShoppingCartModel result = shoppingCartService.updateItemQuantity(7L, 300L, 4);

        assertThat(item.getQuantity()).isEqualTo(4);

        CartItemModel model = result.getItems().getFirst();
        assertThat(model.getQuantity()).isEqualTo(4);
        assertThat(model.getLineTotal()).isEqualByComparingTo("6000.00");
        assertThat(result.getTotalAmount()).isEqualByComparingTo("6000.00");
    }

    @Test
    void updateItemQuantity_shouldThrowWhenQuantityNonPositive() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> shoppingCartService.updateItemQuantity(1L, 2L, 0)
        );

        assertThat(ex.getMessage()).isEqualTo("Quantity must be positive");
    }

    @Test
    void updateItemQuantity_shouldThrowWhenCartNotFound() {
        when(shoppingCartRepository.findByIdWithItems(99L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> shoppingCartService.updateItemQuantity(99L, 1L, 1)
        );

        assertThat(ex.getMessage()).isEqualTo("Cart with id 99 not found");
    }

    @Test
    void updateItemQuantity_shouldThrowWhenItemNotFoundInCart() {
        IndividualClient client = new IndividualClient("CLIENT-1");
        ShoppingCart cart = new ShoppingCart(client);
        cart.setId(8L);
        cart.setItems(new ArrayList<>());

        when(shoppingCartRepository.findByIdWithItems(8L)).thenReturn(Optional.of(cart));

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> shoppingCartService.updateItemQuantity(8L, 123L, 1)
        );

        assertThat(ex.getMessage()).isEqualTo("Item with id 123 not found in cart 8");
    }

    @Test
    void removeItem_shouldRemoveItemFromCart() {
        IndividualClient client = new IndividualClient("CLIENT-1");

        ShoppingCart cart = new ShoppingCart(client);
        cart.setId(11L);

        CartItem item1 = new CartItem(ProductType.MID_RANGE_PHONE, 1);
        item1.setId(400L);
        CartItem item2 = new CartItem(ProductType.LAPTOP, 2);
        item2.setId(401L);

        cart.setItems(new ArrayList<>(List.of(item1, item2)));

        when(shoppingCartRepository.findByIdWithItems(11L)).thenReturn(Optional.of(cart));
        when(shoppingCartRepository.save(any(ShoppingCart.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(priceCalculator.getUnitPrice(client, ProductType.LAPTOP))
                .thenReturn(new BigDecimal("1200.00"));

        ShoppingCartModel result = shoppingCartService.removeItem(11L, 400L);

        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().getFirst().getId()).isEqualTo(401L);

        assertThat(result.getItems()).hasSize(1);
        CartItemModel remaining = result.getItems().getFirst();
        assertThat(remaining.getId()).isEqualTo(401L);
        assertThat(result.getTotalAmount()).isEqualByComparingTo("2400.00");
    }

    @Test
    void removeItem_shouldThrowWhenCartNotFound() {
        when(shoppingCartRepository.findByIdWithItems(77L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> shoppingCartService.removeItem(77L, 1L)
        );

        assertThat(ex.getMessage()).isEqualTo("Cart with id 77 not found");
    }

    @Test
    void removeItem_shouldThrowWhenItemNotFoundInCart() {
        IndividualClient client = new IndividualClient("CLIENT-1");

        ShoppingCart cart = new ShoppingCart(client);
        cart.setId(12L);
        cart.setItems(new ArrayList<>());

        when(shoppingCartRepository.findByIdWithItems(12L)).thenReturn(Optional.of(cart));

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> shoppingCartService.removeItem(12L, 999L)
        );

        assertThat(ex.getMessage()).isEqualTo("Item with id 999 not found in cart 12");
    }
}
