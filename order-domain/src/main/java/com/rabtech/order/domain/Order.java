
package com.rabtech.order.domain;

public class Order {

    private final OrderId id;
    private OrderStatus status;

    public Order() {
        this.id = OrderId.generate();
        this.status = OrderStatus.DRAFT;
    }

    public String getId() {
    return id.value();
}

public OrderId getOrderId() {
    return id;
}

    public OrderStatus getStatus() {
        return status;
    }

    public void submit() {
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException(
                "Only draft orders can be submitted."
            );
        }
        status = OrderStatus.SUBMITTED;
    }

    public void approve() {
        if (status != OrderStatus.SUBMITTED) {
            throw new IllegalStateException(
                "Only submitted orders can be approved."
            );
        }
        status = OrderStatus.APPROVED;
    }

    public void reject() {
        if (status != OrderStatus.SUBMITTED) {
            throw new IllegalStateException(
                "Only submitted orders can be rejected."
            );
        }
        status = OrderStatus.REJECTED;
    }
}
