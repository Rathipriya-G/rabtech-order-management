
package com.rabtech.order.domain;

import java.util.UUID;

public record OrderId(String value) {

    public OrderId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                "Order ID cannot be empty."
            );
        }
    }

    public static OrderId generate() {
        return new OrderId(UUID.randomUUID().toString());
    }
}
