package com.rvk.skycommerce.service;

import com.rvk.skycommerce.exception.NotFoundException;
import com.rvk.skycommerce.model.CartItemModel;
import com.rvk.skycommerce.model.ProductType;
import com.rvk.skycommerce.model.ShoppingCartModel;
import com.rvk.skycommerce.repository.ClientRepository;
import com.rvk.skycommerce.repository.ShoppingCartRepository;
import com.rvk.skycommerce.repository.entity.CartItem;
import com.rvk.skycommerce.repository.entity.Client;
import com.rvk.skycommerce.repository.entity.ShoppingCart;
import com.rvk.skycommerce.service.pricing.PriceCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final ClientRepository clientRepository;
    private final PriceCalculator priceCalculator;

    @Override
    @Transactional
    @CachePut(value = "shoppingCarts", key = "#result.id")
    public ShoppingCartModel createCartForClient(String clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new NotFoundException("Client with id " + clientId + " not found"));

        ShoppingCart cart = new ShoppingCart(client);
        cart.setTotalAmount(BigDecimal.ZERO);

        ShoppingCart saved = shoppingCartRepository.save(cart);
        return toModel(saved);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "shoppingCarts", key = "#cartId")
    public ShoppingCartModel getCart(Long cartId) {
        ShoppingCart cart = shoppingCartRepository.findByIdWithItems(cartId)
                .orElseThrow(() -> new NotFoundException("Cart with id " + cartId + " not found"));
        return toModel(cart);
    }

    @Override
    @Transactional
    @CachePut(value = "shoppingCarts", key = "#cartId")
    public ShoppingCartModel addItem(Long cartId, ProductType productType, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        ShoppingCart cart = shoppingCartRepository.findByIdWithItems(cartId)
                .orElseThrow(() -> new NotFoundException("Cart with id " + cartId + " not found"));

        CartItem existing = cart.getItems().stream()
                .filter(i -> i.getProductType() == productType)
                .findFirst()
                .orElse(null);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
        } else {
            CartItem item = new CartItem(productType, quantity);
            cart.getItems().add(item);
        }

        updateCartTotal(cart);

        ShoppingCart saved = shoppingCartRepository.save(cart);
        return toModel(saved);
    }

    @Override
    @Transactional
    @CachePut(value = "shoppingCarts", key = "#cartId")
    public ShoppingCartModel updateItemQuantity(Long cartId, Long itemId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        ShoppingCart cart = shoppingCartRepository.findByIdWithItems(cartId)
                .orElseThrow(() -> new NotFoundException("Cart with id " + cartId + " not found"));

        CartItem item = cart.getItems().stream()
                .filter(i -> itemId.equals(i.getId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " not found in cart " + cartId));

        item.setQuantity(quantity);

        updateCartTotal(cart);

        ShoppingCart saved = shoppingCartRepository.save(cart);
        return toModel(saved);
    }

    @Override
    @Transactional
    @CachePut(value = "shoppingCarts", key = "#cartId")
    public ShoppingCartModel removeItem(Long cartId, Long itemId) {
        ShoppingCart cart = shoppingCartRepository.findByIdWithItems(cartId)
                .orElseThrow(() -> new NotFoundException("Cart with id " + cartId + " not found"));

        boolean removed = cart.getItems().removeIf(i -> itemId.equals(i.getId()));
        if (!removed) {
            throw new NotFoundException("Item with id " + itemId + " not found in cart " + cartId);
        }

        updateCartTotal(cart);

        ShoppingCart saved = shoppingCartRepository.save(cart);
        return toModel(saved);
    }

    private void updateCartTotal(ShoppingCart cart) {
        Client client = cart.getClient();
        BigDecimal total = calculateTotal(client, cart.getItems());
        cart.setTotalAmount(total);
    }

    public BigDecimal calculateTotal(Client client, List<CartItem> items) {
        return items.stream()
                .map(item -> {
                    BigDecimal unitPrice = priceCalculator.getUnitPrice(client, item.getProductType());
                    return unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<CartItemModel> buildItemModels(Client client, List<CartItem> items) {
        return items.stream()
                .map(item -> {
                    BigDecimal unitPrice = priceCalculator.getUnitPrice(client, item.getProductType());
                    BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
                    return CartItemModel.builder()
                            .id(item.getId())
                            .productType(item.getProductType())
                            .quantity(item.getQuantity())
                            .unitPrice(unitPrice)
                            .lineTotal(lineTotal)
                            .build();
                })
                .toList();
    }

    private ShoppingCartModel toModel(ShoppingCart cart) {
        Client client = cart.getClient();
        List<CartItemModel> itemModels = buildItemModels(client, cart.getItems());
        BigDecimal total = cart.getTotalAmount() != null
                ? cart.getTotalAmount()
                : calculateTotal(client, cart.getItems());

        return ShoppingCartModel.builder()
                .id(cart.getId())
                .clientId(client != null ? client.getId() : null)
                .items(itemModels)
                .totalAmount(total)
                .build();
    }
}
