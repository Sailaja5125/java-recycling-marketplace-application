package com.example.application.Repository;

import com.example.application.Model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Returns all orders placed by the given user.
     */
    List<Order> findByUserId(Long userId);
    List<Order> findByProductIdIn(List<Long> productIds);
    List<Order> findByDeliveryStatus(String deliveryStatus);
    List<Order> findByDeliveryStatusOrDeliveryStatusIsNull(String deliveryStatus);
    List<Order> findByAssignedDeliveryBoyId(Long assignedDeliveryBoyId);
}

