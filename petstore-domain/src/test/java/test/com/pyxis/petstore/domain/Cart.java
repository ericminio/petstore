package test.com.pyxis.petstore.domain;

import com.pyxis.petstore.domain.Item;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cart {

    private final List<CartItem> cartItems = new ArrayList<CartItem>();

    public boolean isEmpty() {
        return cartItems.isEmpty();
    }

    public BigDecimal getSubTotal() {
        BigDecimal subTotal = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            subTotal = subTotal.add(cartItem.getTotalPrice());
        }
        return subTotal;
    }

    public List<CartItem> getItems() {
        return Collections.unmodifiableList(cartItems);
    }

    public void add(Item item) {
        CartItem cartItem = findCartItemFor(item);
        if (cartItem != null) {
            cartItem.incrementQuantity();
        } else {
            cartItems.add(new CartItem(item));
        }
    }

    private CartItem findCartItemFor(Item item) {
        for (CartItem cartItem : cartItems) {
            if (cartItem.isFor(item)) return cartItem;
        }
        return null;
    }
}