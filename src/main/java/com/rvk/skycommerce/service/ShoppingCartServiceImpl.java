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
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final ClientRepository clientRepository;
    private final PriceCalculator priceCalculator;

    @Override
    @Transactional
    @CachePut(value = "shoppingCarts", key = "#result.id")
    public ShoppingCartModel createCartForClient(String clientId) {

        log.info("Creating shopping cart for client={}", clientId);

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> {
                    log.warn("Client with id {} not found", clientId);
                    return new NotFoundException("Client with id " + clientId + " not found");
                });

        ShoppingCart cart = new ShoppingCart(client);
        cart.setTotalAmount(BigDecimal.ZERO);

        ShoppingCart saved = shoppingCartRepository.save(cart);
        log.debug("Created shopping cart id={} for client={}", saved.getId(), clientId);
        return toModel(saved);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "shoppingCarts", key = "#cartId")
    public ShoppingCartModel getCart(Long cartId) {

        log.debug("Fetching shopping cart id={}", cartId);

        ShoppingCart cart = shoppingCartRepository.findByIdWithItems(cartId)
                .orElseThrow(() -> {
                    log.warn("Cart with id {} not found", cartId);
                    return new NotFoundException("Cart with id " + cartId + " not found");
                });
        return toModel(cart);
    }

    @Override
    @Transactional
    @CachePut(value = "shoppingCarts", key = "#cartId")
    public ShoppingCartModel addItem(Long cartId, ProductType productType, int quantity) {

        log.info("Adding item to cartId={} productType={} quantity={}", cartId, productType, quantity);

        if (quantity <= 0) {
            log.warn("Invalid quantity {} for cartId={}", quantity, cartId);
            throw new IllegalArgumentException("Quantity must be positive");
        }

        ShoppingCart cart = shoppingCartRepository.findByIdWithItems(cartId)
                .orElseThrow(() -> {
                    log.warn("Cart with id {} not found", cartId);
                    return new NotFoundException("Cart with id " + cartId + " not found");
                });

        CartItem existing = cart.getItems().stream()
                .filter(i -> i.getProductType() == productType)
                .findFirst()
                .orElse(null);

        if (existing != null) {
            int newQty = existing.getQuantity() + quantity;
            existing.setQuantity(newQty);
            log.debug("Updated existing item id={} newQuantity={} in cartId={}", existing.getId(), newQty, cartId);
        } else {
            CartItem item = new CartItem(productType, quantity);
            cart.getItems().add(item);
            log.debug("Added new item productType={} quantity={} to cartId={}", productType, quantity, cartId);
        }

        updateCartTotal(cart);

        ShoppingCart saved = shoppingCartRepository.save(cart);

        log.info("Item added to cartId={} savedCartId={} total={}", cartId, saved.getId(), saved.getTotalAmount());

        return toModel(saved);
    }

    @Override
    @Transactional
    @CachePut(value = "shoppingCarts", key = "#cartId")
    public ShoppingCartModel updateItemQuantity(Long cartId, Long itemId, int quantity) {

        log.info("Updating item quantity cartId={} itemId={} quantity={}", cartId, itemId, quantity);

        if (quantity <= 0) {
            log.warn("Invalid quantity {} for cartId={} itemId={}", quantity, cartId, itemId);
            throw new IllegalArgumentException("Quantity must be positive");
        }

        ShoppingCart cart = shoppingCartRepository.findByIdWithItems(cartId)
                .orElseThrow(() -> {
                    log.warn("Cart with id {} not found", cartId);
                    return new NotFoundException("Cart with id " + cartId + " not found");
                });

        CartItem item = cart.getItems().stream()
                .filter(i -> itemId.equals(i.getId()))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Item with id {} not found in cart {}", itemId, cartId);
                    return new NotFoundException("Item with id " + itemId + " not found in cart " + cartId);
                });

        item.setQuantity(quantity);

        log.debug("Set quantity={} for itemId={} in cartId={}", quantity, itemId, cartId);

        updateCartTotal(cart);

        ShoppingCart saved = shoppingCartRepository.save(cart);

        log.info("Updated item quantity cartId={} itemId={} total={}", cartId, itemId, saved.getTotalAmount());

        return toModel(saved);
    }

    @Override
    @Transactional
    @CachePut(value = "shoppingCarts", key = "#cartId")
    public ShoppingCartModel removeItem(Long cartId, Long itemId) {

        log.info("Removing item itemId={} from cartId={}", itemId, cartId);

        ShoppingCart cart = shoppingCartRepository.findByIdWithItems(cartId)
                .orElseThrow(() -> {
                    log.warn("Cart with id {} not found", cartId);
                    return new NotFoundException("Cart with id " + cartId + " not found");
                });

        boolean removed = cart.getItems().removeIf(i -> itemId.equals(i.getId()));
        if (!removed) {
            log.warn("Item with id {} not found in cart {}", itemId, cartId);
            throw new NotFoundException("Item with id " + itemId + " not found in cart " + cartId);
        }

        updateCartTotal(cart);

        ShoppingCart saved = shoppingCartRepository.save(cart);

        log.info("Removed item itemId={} from cartId={} newTotal={}", itemId, cartId, saved.getTotalAmount());

        return toModel(saved);
    }

    private void updateCartTotal(ShoppingCart cart) {
        Client client = cart.getClient();
        BigDecimal total = calculateTotal(client, cart.getItems());
        cart.setTotalAmount(total);

        log.debug("Updated cart total cartId={} total={}", cart.getId(), total);
    }

    public BigDecimal calculateTotal(Client client, List<CartItem> items) {
        BigDecimal total = items.stream()
                .map(item -> {
                    BigDecimal unitPrice = priceCalculator.getUnitPrice(client, item.getProductType());
                    return unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.debug("Calculated total for clientId={} itemsCount={} total={}", client.getId(), items.size(), total);

        return total;
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

        log.debug("Mapping cart id={} to model total={}", cart.getId(), total);

        return ShoppingCartModel.builder()
                .id(cart.getId())
                .clientId(client != null ? client.getId() : null)
                .items(itemModels)
                .totalAmount(total)
                .build();
    }
}
