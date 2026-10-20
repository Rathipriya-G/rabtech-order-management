
package com.rabtech.order.domain;

import java.util.Objects;

public record PaymentRecorded(OrderId orderId) implements OrderEvent {

    public PaymentRecorded {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
    }
}
