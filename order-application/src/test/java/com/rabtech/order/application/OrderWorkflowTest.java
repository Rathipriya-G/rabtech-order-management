
package com.rabtech.order.application;

import com.rabtech.order.domain.Order;
import com.rabtech.order.domain.OrderStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderWorkflowTest {

    @Test
    void newOrderShouldStartAsDraft() {
        Order order = new Order();

        assertEquals(OrderStatus.DRAFT, order.getStatus());
    }

    @Test
    void submittedOrderCanBeApproved() {
        Order order = new Order();

        order.submit();
        order.approve();

        assertEquals(OrderStatus.APPROVED, order.getStatus());
    }

    @Test
    void draftOrderCannotBeApprovedDirectly() {
        Order order = new Order();

        assertThrows(
            IllegalStateException.class,
            order::approve
        );

        assertEquals(OrderStatus.DRAFT, order.getStatus());
    }

    @Test
    void approvedOrderCannotBeSubmittedAgain() {
        Order order = new Order();

        order.submit();
        order.approve();

        assertThrows(
            IllegalStateException.class,
            order::submit
        );

        assertEquals(OrderStatus.APPROVED, order.getStatus());
    }

    @Test
    void submittedOrderCanBeRejected() {
        Order order = new Order();

        order.submit();
        order.reject();

        assertEquals(OrderStatus.REJECTED, order.getStatus());
    }
}
