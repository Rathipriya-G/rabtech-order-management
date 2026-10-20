
package com.rabtech.order.application;

import com.rabtech.order.domain.Order;
import com.rabtech.order.domain.OrderEvent;
import com.rabtech.order.domain.OrderId;

public class OrderWorkflowService {

    private final OrderRepository repository;
    private final OrderNotification notification;
    private final TimeProvider timeProvider;
    private final OrderEventPublisher eventPublisher;

    public OrderWorkflowService(
            OrderRepository repository,
            OrderNotification notification,
            TimeProvider timeProvider,
            OrderEventPublisher eventPublisher) {
        this.repository = repository;
        this.notification = notification;
        this.timeProvider = timeProvider;
        this.eventPublisher = eventPublisher;
    }

    public Order createOrder() {
        Order order = new Order(OrderId.generate());
        repository.save(order);
        return order;
    }

    public Order confirmOrder(String orderId) {
        Order order = getOrder(orderId);
        order.confirm();
        return recordChange(order);
    }

    public Order recordPayment(String orderId) {
        Order order = getOrder(orderId);
        order.recordPayment();
        return recordChange(order);
    }

    public Order cancelOrder(String orderId) {
        Order order = getOrder(orderId);
        order.cancel();
        return recordChange(order);
    }

    private Order getOrder(String orderId) {
        return repository.findById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Order not found: " + orderId));
    }

    private Order recordChange(Order order) {
        repository.save(order);

        int eventCount = order.getDomainEvents().size();

        if (eventCount > 0) {
            OrderEvent latestEvent =
                    order.getDomainEvents().get(eventCount - 1);
            eventPublisher.publish(latestEvent);
        }

        switch (order.getStatus()) {
            case CONFIRMED -> notification.notifyApproved(order);
            case PAID -> notification.notifySubmitted(order);
            case CANCELLED -> notification.notifyRejected(order);
            default -> {
                // No notification is needed for DRAFT.
            }
        }

        return order;
    }
}
