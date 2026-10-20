
package com.rabtech.order.infrastructure;

import com.rabtech.order.application.OrderNotification;
import com.rabtech.order.domain.Order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InMemoryOrderNotification implements OrderNotification {

    private final List<String> notifications = new ArrayList<>();

    @Override
    public void notifySubmitted(Order order) {
        notifications.add("Order submitted: " + order.getId());
    }

    @Override
    public void notifyApproved(Order order) {
        notifications.add("Order approved: " + order.getId());
    }

    @Override
    public void notifyRejected(Order order) {
        notifications.add("Order rejected: " + order.getId());
    }

    public List<String> getNotifications() {
        return Collections.unmodifiableList(notifications);
    }
}
