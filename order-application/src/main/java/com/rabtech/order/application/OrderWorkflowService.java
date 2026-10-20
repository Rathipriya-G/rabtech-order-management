
package com.rabtech.order.application;

import com.rabtech.order.domain.Order;
import com.rabtech.order.domain.OrderEvent;

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
        Order order = new Order();
        repository.save(order);
        return order;
    }

    public Order submitOrder(String orderId) {
        Order order = getOrder(orderId);
        order.submit();
        return recordChange(order, "SUBMITTED");
    }

    public Order approveOrder(String orderId) {
        Order order = getOrder(orderId);
        order.approve();
        return recordChange(order, "APPROVED");
    }

    public Order rejectOrder(String orderId) {
        Order order = getOrder(orderId);
        order.reject();
        return recordChange(order, "REJECTED");
    }

    public Order handle(SubmitOrderCommand command) {
        return submitOrder(command.orderId());
    }

    public Order handle(ApproveOrderCommand command) {
        return approveOrder(command.orderId());
    }

    public Order handle(RejectOrderCommand command) {
        return rejectOrder(command.orderId());
    }

    private Order getOrder(String orderId) {
        return repository.findById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Order not found: " + orderId));
    }

    private Order recordChange(Order order, String action) {
        repository.save(order);

        switch (action) {
            case "SUBMITTED" ->
                    notification.notifySubmitted(order);
            case "APPROVED" ->
                    notification.notifyApproved(order);
            case "REJECTED" ->
                    notification.notifyRejected(order);
            default -> throw new IllegalArgumentException(
                    "Unknown order action: " + action);
        }

        eventPublisher.publish(new OrderEvent(
                order.getId(),
                order.getStatus(),
                timeProvider.now()
        ));

        return order;
    }
}
