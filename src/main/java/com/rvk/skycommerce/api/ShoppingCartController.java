package com.rvk.skycommerce.api;

import com.rvk.skycommerce.api.dto.cart.AddCartItemsRequest;
import com.rvk.skycommerce.api.dto.cart.CartResponse;
import com.rvk.skycommerce.api.dto.cart.UpdateCartItemQuantityRequest;
import com.rvk.skycommerce.api.mapper.CartItemResponseMapper;
import com.rvk.skycommerce.model.ShoppingCartModel;
import com.rvk.skycommerce.service.ShoppingCartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    @PostMapping
    public CartResponse createCart(@RequestParam String clientId) {
        ShoppingCartModel model = shoppingCartService.createCartForClient(clientId);
        return CartItemResponseMapper.fromModel(model);
    }

    @GetMapping("/{cartId}")
    public CartResponse getCart(@PathVariable Long cartId) {
        ShoppingCartModel model = shoppingCartService.getCart(cartId);
        return CartItemResponseMapper.fromModel(model);
    }

    @PostMapping("/{cartId}/items")
    public CartResponse addItems(@PathVariable Long cartId,
                                 @Valid @RequestBody AddCartItemsRequest request) {

        request.getItems().forEach(item ->
                shoppingCartService.addItem(cartId, item.getProductType(), item.getQuantity())
        );

        ShoppingCartModel updated = shoppingCartService.getCart(cartId);
        return CartItemResponseMapper.fromModel(updated);
    }

    @PutMapping("/{cartId}/items/{itemId}")
    public CartResponse updateItemQuantity(@PathVariable Long cartId,
                                           @PathVariable Long itemId,
                                           @Valid @RequestBody UpdateCartItemQuantityRequest request) {
        ShoppingCartModel model = shoppingCartService.updateItemQuantity(cartId, itemId, request.getQuantity());
        return CartItemResponseMapper.fromModel(model);
    }

    @DeleteMapping("/{cartId}/items/{itemId}")
    public CartResponse removeItem(@PathVariable Long cartId,
                                   @PathVariable Long itemId) {
        ShoppingCartModel model = shoppingCartService.removeItem(cartId, itemId);
        return CartItemResponseMapper.fromModel(model);
    }
}
