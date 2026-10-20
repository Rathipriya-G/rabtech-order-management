
package com.rabtech.order.infrastructure;

import com.rabtech.order.application.OrderWorkflowService;
import com.rabtech.order.domain.Order;
import com.rabtech.order.domain.OrderCancelled;
import com.rabtech.order.domain.OrderConfirmed;
import com.rabtech.order.domain.OrderLine;
import com.rabtech.order.domain.OrderStatus;
import com.rabtech.order.domain.PaymentRecorded;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class OrderWorkflowServiceTest {

    @Test
    void workflowShouldSaveOrderAndPublishRequiredEvents() {
        InMemoryOrderRepository repository =
                new InMemoryOrderRepository();

        InMemoryOrderEventPublisher eventPublisher =
                new InMemoryOrderEventPublisher();

        OrderWorkflowService service = new OrderWorkflowService(
                repository,
                new InMemoryOrderNotification(),
                new InMemoryTimeProvider(Instant.EPOCH),
                eventPublisher
        );

        Order order = service.createOrder();

        assertEquals(OrderStatus.DRAFT, order.getStatus());

        order.addLine(new OrderLine(
                "P1", 2, new BigDecimal("10.00")));

        service.confirmOrder(order.getId().value());

        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
        assertTrue(repository.findById(
                order.getId().value()).isPresent());
        assertEquals(1, eventPublisher.getEvents().size());
        assertTrue(eventPublisher.getEvents().get(0)
                instanceof OrderConfirmed);

        service.recordPayment(order.getId().value());

        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(2, eventPublisher.getEvents().size());
        assertTrue(eventPublisher.getEvents().get(1)
                instanceof PaymentRecorded);
    }

    @Test
    void cancelledOrderCannotBePaid() {
        InMemoryOrderRepository repository =
                new InMemoryOrderRepository();

        InMemoryOrderEventPublisher eventPublisher =
                new InMemoryOrderEventPublisher();

        OrderWorkflowService service = new OrderWorkflowService(
                repository,
                new InMemoryOrderNotification(),
                new InMemoryTimeProvider(Instant.EPOCH),
                eventPublisher
        );

        Order order = service.createOrder();

        service.cancelOrder(order.getId().value());

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(1, eventPublisher.getEvents().size());
        assertTrue(eventPublisher.getEvents().get(0)
                instanceof OrderCancelled);

        assertThrows(
                IllegalStateException.class,
                () -> service.recordPayment(order.getId().value()));

        assertEquals(1, eventPublisher.getEvents().size());
    }

    @Test
    void unknownOrderShouldBeRejected() {
        OrderWorkflowService service = new OrderWorkflowService(
                new InMemoryOrderRepository(),
                new InMemoryOrderNotification(),
                new InMemoryTimeProvider(Instant.EPOCH),
                new InMemoryOrderEventPublisher()
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.confirmOrder("missing-order"));
    }
}
