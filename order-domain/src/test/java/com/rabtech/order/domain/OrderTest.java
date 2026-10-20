package com.rabtech.order.domain;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    private Order newOrderWithLine() {
        Order order = new Order(OrderId.generate());
        order.addLine(new OrderLine("P1", 2, new BigDecimal("10.00")));
        return order;
    }

    @Test
    void newOrderStartsAsDraft() {
        assertEquals(OrderStatus.DRAFT,
            new Order(OrderId.generate()).getStatus());
    }

    @Test
    void cannotConfirmAnEmptyOrder() {
        Order order = new Order(OrderId.generate());
        assertThrows(IllegalStateException.class, order::confirm);
    }

    @Test
    void confirmationChangesStatusAndRecordsEvent() {
        Order order = newOrderWithLine();
        order.confirm();

        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
        assertTrue(order.getDomainEvents().get(0) instanceof OrderConfirmed);
    }

    @Test
    void quantityMustBePositive() {
        assertThrows(IllegalArgumentException.class,
            () -> new OrderLine("P1", 0, BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class,
            () -> new OrderLine("P1", -1, BigDecimal.ONE));
    }

    @Test
    void totalUsesQuantityAndUnitPrice() {
        Order order = newOrderWithLine();
        assertEquals(0, new BigDecimal("20.00").compareTo(order.getTotal()));
    }

    @Test
    void confirmedOrderCanBePaidAndRecordsEvent() {
        Order order = newOrderWithLine();
        order.confirm();
        order.recordPayment();

        assertEquals(OrderStatus.PAID, order.getStatus());
        assertTrue(order.getDomainEvents().stream()
            .anyMatch(event -> event instanceof PaymentRecorded));
    }

    @Test
    void cancelledOrderCannotBePaid() {
        Order order = newOrderWithLine();
        order.cancel();

        assertThrows(IllegalStateException.class, order::recordPayment);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void cancellationRecordsEvent() {
        Order order = newOrderWithLine();
        order.cancel();

        assertTrue(order.getDomainEvents().stream()
            .anyMatch(event -> event instanceof OrderCancelled));
    }

    @Test
    void cannotAddLinesAfterConfirmation() {
        Order order = newOrderWithLine();
        order.confirm();

        assertThrows(IllegalStateException.class,
            () -> order.addLine(
                new OrderLine("P2", 1, BigDecimal.ONE)));
    }
}
