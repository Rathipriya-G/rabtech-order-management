
package com.rabtech.order.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Order {

    private final OrderId id;
    private final List<OrderLine> lines = new ArrayList<>();
    private final List<OrderEvent> domainEvents = new ArrayList<>();

    private OrderStatus status = OrderStatus.DRAFT;

    public Order(OrderId id) {
        this.id = Objects.requireNonNull(id, "Order ID cannot be null");
    }

    public OrderId getId() {
        return id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderLine> getLines() {
        return Collections.unmodifiableList(lines);
    }

    public List<OrderEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void addLine(OrderLine line) {
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException(
                "Lines can only be added while the order is in DRAFT"
            );
        }

        lines.add(Objects.requireNonNull(line, "Order line cannot be null"));
    }

    public BigDecimal getTotal() {
        return lines.stream()
            .map(OrderLine::getTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void confirm() {
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException(
                "Only a draft order can be confirmed"
            );
        }

        if (lines.isEmpty()) {
            throw new IllegalStateException(
                "An order must contain at least one line before confirmation"
            );
        }

        status = OrderStatus.CONFIRMED;
        domainEvents.add(new OrderConfirmed(id));
    }

    public void recordPayment() {
        if (status != OrderStatus.CONFIRMED) {
            throw new IllegalStateException(
                "Only a confirmed order can be paid"
            );
        }

        status = OrderStatus.PAID;
        domainEvents.add(new PaymentRecorded(id));
    }

    public void cancel() {
        if (status == OrderStatus.CANCELLED) {
            throw new IllegalStateException(
                "Order is already cancelled"
            );
        }

        status = OrderStatus.CANCELLED;
        domainEvents.add(new OrderCancelled(id));
    }
}
