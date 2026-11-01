package edu.assignment.cart.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public final class CartCalculator {
    private CartCalculator() {}
    public static BigDecimal total(List<CartItem> items) {
        Objects.requireNonNull(items, "items");
        return items.stream().map(CartItem::total).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
