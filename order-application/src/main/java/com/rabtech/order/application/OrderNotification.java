
package com.rabtech.order.application;

import com.rabtech.order.domain.Order;

public interface OrderNotification {

    void notifySubmitted(Order order);

    void notifyApproved(Order order);

    void notifyRejected(Order order);
}
