
package com.rabtech.order.infrastructure;

import com.rabtech.order.application.OrderEventPublisher;
import com.rabtech.order.domain.OrderEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InMemoryOrderEventPublisher implements OrderEventPublisher {

    private final List<OrderEvent> events = new ArrayList<>();

    @Override
    public void publish(OrderEvent event) {
        events.add(event);
    }

    public List<OrderEvent> getEvents() {
        return Collections.unmodifiableList(events);
    }
}
