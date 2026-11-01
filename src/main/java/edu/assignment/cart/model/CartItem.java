package edu.assignment.cart.model;

import java.math.BigDecimal;
import java.util.Objects;

/** Monetary values stay decimal: no binary floating point or hidden rounding. */
public record CartItem(BigDecimal price, int quantity) {
    public CartItem {
        Objects.requireNonNull(price, "price");
        if (price.signum() < 0 || price.stripTrailingZeros().scale() > 2)
            throw new IllegalArgumentException("Price must be nonnegative, with at most two decimal places");
        if (quantity < 0) throw new IllegalArgumentException("Quantity must be nonnegative");
    }
    public BigDecimal total() { return price.multiply(BigDecimal.valueOf(quantity)); }
}
