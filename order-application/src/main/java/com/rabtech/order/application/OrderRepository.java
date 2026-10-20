
package com.rabtech.order.application;

import com.rabtech.order.domain.Order;
import java.util.Optional;

public interface OrderRepository {

    void save(Order order);

    Optional<Order> findById(String id);
}
