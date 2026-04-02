package com.example.application.Controller;

import com.example.application.Model.Order;
import com.example.application.Service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")

public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/buy")
    public ResponseEntity<?> buyProduct(@RequestBody Map<String, Object> request) {
        try {
            Long userId = ((Number) request.get("userId")).longValue();
            Long productId = ((Number) request.get("productId")).longValue();
            int quantity = ((Number) request.getOrDefault("quantity", 1)).intValue();

            Map<String, Object> response = orderService.buyProduct(userId, productId, quantity);
            return ResponseEntity.status(201).body(response);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404).body(e.getMessage());
            }
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getOrdersForUser(@PathVariable Long userId) {
        try {
            List<Order> orders = orderService.getOrdersForUser(userId);
            return ResponseEntity.ok(orders);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @DeleteMapping("/cancel/{orderId}")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId) {
        try {
            Map<String, Object> response = orderService.cancelOrder(orderId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404).body(e.getMessage());
            }
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingOrders() {
        // Here we could add a method in OrderRepository and OrderService to find by deliveryStatus="PENDING"
        // Let's assume orderRepository exists in service or we can fetch them via a new service method
        return ResponseEntity.ok(orderService.getPendingOrders());
    }

    @GetMapping("/delivery/{deliveryBoyId}")
    public ResponseEntity<?> getAssignedOrders(@PathVariable Long deliveryBoyId) {
        return ResponseEntity.ok(orderService.getAssignedOrders(deliveryBoyId));
    }

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<?> getOrdersForSeller(@PathVariable String sellerId) {
        return ResponseEntity.ok(orderService.getOrdersForSeller(sellerId));
    }

    @PostMapping("/{orderId}/assign")
    public ResponseEntity<?> assignDeliveryBoy(@PathVariable Long orderId, @RequestParam Long deliveryBoyId) {
        try {
            orderService.assignDeliveryBoy(orderId, deliveryBoyId);
            return ResponseEntity.ok("Successfully assigned order to delivery boy.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PostMapping("/{orderId}/complete")
    public ResponseEntity<?> completeDelivery(@PathVariable Long orderId, @RequestParam String token) {
        try {
            orderService.completeOrderDelivery(orderId, token);
            return ResponseEntity.ok("Successfully completed order delivery.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}
