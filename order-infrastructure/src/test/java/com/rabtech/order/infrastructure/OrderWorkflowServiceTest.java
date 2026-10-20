
package com.rabtech.order.infrastructure;

import com.rabtech.order.application.ApproveOrderCommand;
import com.rabtech.order.application.OrderWorkflowService;
import com.rabtech.order.application.RejectOrderCommand;
import com.rabtech.order.application.SubmitOrderCommand;
import com.rabtech.order.domain.Order;
import com.rabtech.order.domain.OrderStatus;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class OrderWorkflowServiceTest {

    @Test
    void workflowShouldSaveOrderAndPublishEvents() {
        InMemoryOrderRepository repository =
                new InMemoryOrderRepository();

        InMemoryOrderNotification notification =
                new InMemoryOrderNotification();

        InMemoryTimeProvider timeProvider =
                new InMemoryTimeProvider(
                        Instant.parse("2026-10-20T10:00:00Z"));

        InMemoryOrderEventPublisher eventPublisher =
                new InMemoryOrderEventPublisher();

        OrderWorkflowService service = new OrderWorkflowService(
                repository,
                notification,
                timeProvider,
                eventPublisher
        );

        Order order = service.createOrder();

        assertEquals(OrderStatus.DRAFT, order.getStatus());

        service.handle(new SubmitOrderCommand(order.getId()));
        service.handle(new ApproveOrderCommand(order.getId()));

        assertEquals(OrderStatus.APPROVED, order.getStatus());

        assertTrue(repository.findById(order.getId()).isPresent());

        assertEquals(2, eventPublisher.getEvents().size());
        assertEquals(
                OrderStatus.SUBMITTED,
                eventPublisher.getEvents().get(0).status()
        );
        assertEquals(
                OrderStatus.APPROVED,
                eventPublisher.getEvents().get(1).status()
        );

        assertEquals(2, notification.getNotifications().size());

        assertEquals(
                Instant.parse("2026-10-20T10:00:00Z"),
                eventPublisher.getEvents().get(1).occurredAt()
        );
    }

    @Test
    void unknownOrderShouldBeRejected() {
        InMemoryOrderRepository repository =
                new InMemoryOrderRepository();

        OrderWorkflowService service = new OrderWorkflowService(
                repository,
                new InMemoryOrderNotification(),
                new InMemoryTimeProvider(Instant.EPOCH),
                new InMemoryOrderEventPublisher()
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.handle(
                        new SubmitOrderCommand("missing-order"))
        );
    }

    @Test
    void submittedOrderCanBeRejectedUsingCommand() {
        InMemoryOrderRepository repository =
                new InMemoryOrderRepository();

        OrderWorkflowService service = new OrderWorkflowService(
                repository,
                new InMemoryOrderNotification(),
                new InMemoryTimeProvider(Instant.EPOCH),
                new InMemoryOrderEventPublisher()
        );

        Order order = service.createOrder();

        service.handle(new SubmitOrderCommand(order.getId()));
        service.handle(new RejectOrderCommand(order.getId()));

        assertEquals(OrderStatus.REJECTED, order.getStatus());
    }
}
