
package com.rabtech.order.application;

import com.rabtech.order.domain.OrderEvent;

public interface OrderEventPublisher {

    void publish(OrderEvent event);
}
