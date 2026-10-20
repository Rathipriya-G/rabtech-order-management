
package com.rabtech.order.domain;

import java.time.Instant;

public record OrderEvent(
        String orderId,
        OrderStatus status,
        Instant occurredAt
) {
}
