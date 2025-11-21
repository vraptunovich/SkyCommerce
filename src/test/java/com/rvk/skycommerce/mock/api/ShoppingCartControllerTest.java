package com.rvk.skycommerce.mock.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rvk.skycommerce.api.ShoppingCartController;
import com.rvk.skycommerce.api.dto.cart.AddCartItem;
import com.rvk.skycommerce.api.dto.cart.AddCartItemsRequest;
import com.rvk.skycommerce.api.dto.cart.UpdateCartItemQuantityRequest;
import com.rvk.skycommerce.config.SecurityConfig;
import com.rvk.skycommerce.exception.NotFoundException;
import com.rvk.skycommerce.model.CartItemModel;
import com.rvk.skycommerce.model.ProductType;
import com.rvk.skycommerce.model.ShoppingCartModel;
import com.rvk.skycommerce.service.ShoppingCartService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ShoppingCartController.class)
@Import(SecurityConfig.class)
@WithMockUser(roles = "USER")
class ShoppingCartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShoppingCartService shoppingCartService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createCart_shouldReturnCartResponse() throws Exception {
        ShoppingCartModel model = ShoppingCartModel.builder()
                .id(1L)
                .clientId("CLIENT-1")
                .totalAmount(BigDecimal.ZERO)
                .items(List.of())
                .build();

        given(shoppingCartService.createCartForClient("CLIENT-1")).willReturn(model);

        mockMvc.perform(post("/api/carts")
                        .param("clientId", "CLIENT-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.clientId").value("CLIENT-1"))
                .andExpect(jsonPath("$.totalAmount").value(0))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items").isEmpty());

        verify(shoppingCartService).createCartForClient("CLIENT-1");
    }

    @Test
    void getCart_shouldReturnCartResponse() throws Exception {
        CartItemModel item = CartItemModel.builder()
                .id(10L)
                .productType(ProductType.HIGH_END_PHONE)
                .quantity(2)
                .unitPrice(new BigDecimal("1500.00"))
                .lineTotal(new BigDecimal("3000.00"))
                .build();

        ShoppingCartModel model = ShoppingCartModel.builder()
                .id(2L)
                .clientId("CLIENT-2")
                .totalAmount(new BigDecimal("3000.00"))
                .items(List.of(item))
                .build();

        given(shoppingCartService.getCart(2L)).willReturn(model);

        mockMvc.perform(get("/api/carts/{cartId}", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.clientId").value("CLIENT-2"))
                .andExpect(jsonPath("$.totalAmount").value(3000.00))
                .andExpect(jsonPath("$.items[0].id").value(10L))
                .andExpect(jsonPath("$.items[0].productType").value("HIGH_END_PHONE"))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].unitPrice").value(1500.00))
                .andExpect(jsonPath("$.items[0].lineTotal").value(3000.00));

        verify(shoppingCartService).getCart(2L);
    }

    @Test
    void addItems_shouldCallServiceForEachItemAndReturnUpdatedCart() throws Exception {
        AddCartItem item1 = new AddCartItem(ProductType.HIGH_END_PHONE, 1);
        AddCartItem item2 = new AddCartItem(ProductType.LAPTOP, 2);
        AddCartItemsRequest request = new AddCartItemsRequest(List.of(item1, item2));

        CartItemModel modelItem1 = CartItemModel.builder()
                .id(100L)
                .productType(ProductType.HIGH_END_PHONE)
                .quantity(1)
                .unitPrice(new BigDecimal("1500.00"))
                .lineTotal(new BigDecimal("1500.00"))
                .build();

        CartItemModel modelItem2 = CartItemModel.builder()
                .id(101L)
                .productType(ProductType.LAPTOP)
                .quantity(2)
                .unitPrice(new BigDecimal("1200.00"))
                .lineTotal(new BigDecimal("2400.00"))
                .build();

        ShoppingCartModel updated = ShoppingCartModel.builder()
                .id(5L)
                .clientId("CLIENT-5")
                .totalAmount(new BigDecimal("3900.00"))
                .items(List.of(modelItem1, modelItem2))
                .build();

        given(shoppingCartService.getCart(5L)).willReturn(updated);

        mockMvc.perform(post("/api/carts/{cartId}/items", 5L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.clientId").value("CLIENT-5"))
                .andExpect(jsonPath("$.totalAmount").value(3900.00))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(2));


        verify(shoppingCartService).addItem(eq(5L), eq(ProductType.HIGH_END_PHONE), eq(1));
        verify(shoppingCartService).addItem(eq(5L), eq(ProductType.LAPTOP), eq(2));
        verify(shoppingCartService).getCart(5L);
    }

    @Test
    void updateItemQuantity_shouldReturnUpdatedCart() throws Exception {
        UpdateCartItemQuantityRequest request = new UpdateCartItemQuantityRequest(3);

        CartItemModel item = CartItemModel.builder()
                .id(200L)
                .productType(ProductType.MID_RANGE_PHONE)
                .quantity(3)
                .unitPrice(new BigDecimal("800.00"))
                .lineTotal(new BigDecimal("2400.00"))
                .build();

        ShoppingCartModel model = ShoppingCartModel.builder()
                .id(7L)
                .clientId("CLIENT-7")
                .totalAmount(new BigDecimal("2400.00"))
                .items(List.of(item))
                .build();

        given(shoppingCartService.updateItemQuantity(7L, 200L, 3)).willReturn(model);

        mockMvc.perform(put("/api/carts/{cartId}/items/{itemId}", 7L, 200L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7L))
                .andExpect(jsonPath("$.clientId").value("CLIENT-7"))
                .andExpect(jsonPath("$.totalAmount").value(2400.00))
                .andExpect(jsonPath("$.items[0].id").value(200L))
                .andExpect(jsonPath("$.items[0].quantity").value(3));

        verify(shoppingCartService).updateItemQuantity(7L, 200L, 3);
    }

    @Test
    void removeItem_shouldReturnUpdatedCart() throws Exception {
        CartItemModel item = CartItemModel.builder()
                .id(300L)
                .productType(ProductType.LAPTOP)
                .quantity(1)
                .unitPrice(new BigDecimal("1200.00"))
                .lineTotal(new BigDecimal("1200.00"))
                .build();

        ShoppingCartModel model = ShoppingCartModel.builder()
                .id(9L)
                .clientId("CLIENT-9")
                .totalAmount(new BigDecimal("1200.00"))
                .items(List.of(item))
                .build();

        given(shoppingCartService.removeItem(9L, 400L)).willReturn(model);

        mockMvc.perform(delete("/api/carts/{cartId}/items/{itemId}", 9L, 400L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(9L))
                .andExpect(jsonPath("$.clientId").value("CLIENT-9"))
                .andExpect(jsonPath("$.totalAmount").value(1200.00))
                .andExpect(jsonPath("$.items[0].id").value(300L));

        verify(shoppingCartService).removeItem(9L, 400L);
    }

    @Test
    void getCart_shouldReturn404WhenNotFound() throws Exception {
        given(shoppingCartService.getCart(123L))
                .willThrow(new NotFoundException("Cart with id 123 not found"));

        mockMvc.perform(get("/api/carts/{cartId}", 123L))
                .andExpect(status().isNotFound());

        verify(shoppingCartService).getCart(123L);
    }
}
