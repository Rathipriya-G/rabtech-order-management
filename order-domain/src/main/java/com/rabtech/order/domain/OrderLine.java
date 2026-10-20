
package com.rabtech.order.domain;

import java.math.BigDecimal;
import java.util.Objects;

public final class OrderLine {

    private final String productId;
    private final int quantity;
    private final BigDecimal unitPrice;

    public OrderLine(String productId, int quantity, BigDecimal unitPrice) {
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("Product ID cannot be blank");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                "Quantity must be a positive whole number"
            );
        }

        Objects.requireNonNull(unitPrice, "Unit price cannot be null");

        if (unitPrice.signum() < 0) {
            throw new IllegalArgumentException(
                "Unit price cannot be negative"
            );
        }

        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
