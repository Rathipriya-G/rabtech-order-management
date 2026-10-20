
package com.rabtech.order.domain;

import java.util.Objects;

public record OrderCancelled(OrderId orderId) implements OrderEvent {

    public OrderCancelled {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
    }
}
