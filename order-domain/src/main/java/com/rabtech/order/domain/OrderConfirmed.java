
package com.rabtech.order.domain;

import java.util.Objects;

public record OrderConfirmed(OrderId orderId) implements OrderEvent {

    public OrderConfirmed {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
    }
}
