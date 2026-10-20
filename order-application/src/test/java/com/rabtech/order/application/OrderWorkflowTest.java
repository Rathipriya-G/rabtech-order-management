
package com.rabtech.order.application;

import com.rabtech.order.domain.Order;
import com.rabtech.order.domain.OrderCancelled;
import com.rabtech.order.domain.OrderConfirmed;
import com.rabtech.order.domain.OrderId;
import com.rabtech.order.domain.OrderLine;
import com.rabtech.order.domain.OrderStatus;
import com.rabtech.order.domain.PaymentRecorded;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderWorkflowTest {

    @Test
    void newOrderShouldStartAsDraft() {
        Order order = new Order(OrderId.generate());

        assertEquals(OrderStatus.DRAFT, order.getStatus());
    }

    @Test
    void emptyOrderCannotBeConfirmed() {
        Order order = new Order(OrderId.generate());

        assertThrows(IllegalStateException.class, order::confirm);

        assertEquals(OrderStatus.DRAFT, order.getStatus());
    }

    @Test
    void orderWithLineCanBeConfirmed() {
        Order order = new Order(OrderId.generate());
        order.addLine(new OrderLine(
            "P1", 2, new BigDecimal("10.00")
        ));

        order.confirm();

        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
        assertTrue(order.getDomainEvents().get(0) instanceof OrderConfirmed);
    }

    @Test
    void quantityMustBePositive() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new OrderLine("P1", 0, new BigDecimal("10.00"))
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> new OrderLine("P1", -1, new BigDecimal("10.00"))
        );
    }

    @Test
    void orderTotalIsCalculatedFromLinePricesAndQuantities() {
        Order order = new Order(OrderId.generate());

        order.addLine(new OrderLine(
            "P1", 2, new BigDecimal("10.50")
        ));
        order.addLine(new OrderLine(
            "P2", 1, new BigDecimal("3.00")
        ));

        assertEquals(
            0,
            new BigDecimal("24.00").compareTo(order.getTotal())
        );
    }

    @Test
    void confirmedOrderCanBePaid() {
        Order order = new Order(OrderId.generate());
        order.addLine(new OrderLine(
            "P1", 1, new BigDecimal("10.00")
        ));

        order.confirm();
        order.recordPayment();

        assertEquals(OrderStatus.PAID, order.getStatus());
        assertTrue(
            order.getDomainEvents().stream()
                .anyMatch(event -> event instanceof PaymentRecorded)
        );
    }

    @Test
    void cancelledOrderCannotBePaid() {
        Order order = new Order(OrderId.generate());
        order.addLine(new OrderLine(
            "P1", 1, new BigDecimal("10.00")
        ));

        order.cancel();

        assertThrows(IllegalStateException.class, order::recordPayment);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void cancellingOrderRecordsCancellationEvent() {
        Order order = new Order(OrderId.generate());

        order.cancel();

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertTrue(
            order.getDomainEvents().stream()
                .anyMatch(event -> event instanceof OrderCancelled)
        );
    }
}
