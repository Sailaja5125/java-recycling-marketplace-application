package com.example.application.Service;

import com.example.application.Model.Order;
import com.example.application.Model.Product;
import com.example.application.Model.User;
import com.example.application.Model.Role;
import com.example.application.Repository.OrderRepository;
import com.example.application.Repository.ProductRepository;
import com.example.application.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;

import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public Map<String, Object> buyProduct(Long userId, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (product.getQuantity() < quantity) {
            throw new IllegalArgumentException("Not enough stock available. Available quantity: " + product.getQuantity());
        }

        double totalCost = product.getPrice() * quantity;
        int requiredPoints = (int) Math.ceil(totalCost);

        if (user.getRewards() < requiredPoints) {
            throw new IllegalArgumentException("Not enough reward points. Required: " + requiredPoints + ", available: " + user.getRewards());
        }

        // Deduct reward points and decrease stock
        user.setRewards(user.getRewards() - requiredPoints);
        product.setQuantity(product.getQuantity() - quantity);

        // Create order record
        Order order = new Order();
        order.setUserId(user.getId());
        order.setProductId(product.getId());
        order.setProductName(product.getProductName());
        order.setQuantity(quantity);
        order.setPrice(totalCost);
        order.setStatus("PLACED");
        order.setDeliveryStatus("PENDING");
        
        // Generate a 6-digit OTP token for the order pickup/delivery
        String token = String.format("%06d", new java.util.Random().nextInt(999999));
        order.setCompletionToken(token);

        orderRepository.save(order);
        userRepository.save(user);
        productRepository.save(product);

        Map<String, Object> response = new HashMap<>();
        response.put("orderId", order.getId());
        response.put("userId", user.getId());
        response.put("productId", product.getId());
        response.put("productName", product.getProductName());
        response.put("totalCostPoints", requiredPoints);
        response.put("remainingRewards", user.getRewards());
        response.put("product", product);
        response.put("completionToken", token); // Return to frontend

        return response;
    }

    public List<Order> getOrdersForUser(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        return orderRepository.findByUserId(userId);
    }

    public Map<String, Object> cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!"PLACED".equalsIgnoreCase(order.getStatus())) {
            throw new IllegalArgumentException("Order cannot be cancelled (status: " + order.getStatus() + ")");
        }

        User user = userRepository.findById(order.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found for this order"));

        Product product = productRepository.findById(order.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found for this order"));

        // Refund reward points and restore stock
        int refundedPoints = (int) Math.ceil(order.getPrice());
        user.setRewards(user.getRewards() + refundedPoints);
        product.setQuantity(product.getQuantity() + order.getQuantity());

        order.setStatus("CANCELLED");

        userRepository.save(user);
        productRepository.save(product);
        orderRepository.save(order);

        Map<String, Object> response = new HashMap<>();
        response.put("orderId", order.getId());
        response.put("status", order.getStatus());
        response.put("refundedPoints", refundedPoints);
        response.put("currentRewards", user.getRewards());
        response.put("productId", product.getId());
        response.put("productQuantity", product.getQuantity());

        return response;
    }

    public List<Map<String, Object>> getOrdersForSeller(String sellerId) {
        List<Product> sellerProducts = productRepository.findBySellerId(sellerId);
        if (sellerProducts.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> productIds = sellerProducts.stream()
                .map(Product::getId)
                .collect(Collectors.toList());

        List<Order> orders = orderRepository.findByProductIdIn(productIds);

        return orders.stream().map(order -> {
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("order", order);

            userRepository.findById(order.getUserId()).ifPresent(user -> {
                Map<String, String> userInfo = new HashMap<>();
                userInfo.put("name", user.getUsername());
                userInfo.put("email", user.getEmail());
                orderMap.put("buyer", userInfo);
            });

            return orderMap;
        }).collect(Collectors.toList());
    }

    public List<Order> getPendingOrders() {
        return orderRepository.findByDeliveryStatus("PENDING");
    }

    public List<Order> getAssignedOrders(Long deliveryBoyId) {
        return orderRepository.findByAssignedDeliveryBoyId(deliveryBoyId);
    }

    @Transactional
    public void assignDeliveryBoy(Long orderId, Long deliveryBoyId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderId));
        
        User deliveryBoy = userRepository.findById(deliveryBoyId)
                .orElseThrow(() -> new IllegalArgumentException("Delivery Boy not found"));

        if (!Role.DELIVERY_BOY.equals(deliveryBoy.getRole())) {
            throw new IllegalArgumentException("Assigned user is not a delivery boy");
        }

        if (!"PENDING".equals(order.getDeliveryStatus())) {
            throw new IllegalArgumentException("Order is not pending delivery. Status: " + order.getDeliveryStatus());
        }
        
        order.setAssignedDeliveryBoyId(deliveryBoyId);
        order.setDeliveryStatus("ASSIGNED");
        orderRepository.save(order);
    }

    @Transactional
    public void completeOrderDelivery(Long orderId, String token) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderId));
        
        if (!"ASSIGNED".equals(order.getDeliveryStatus())) {
            throw new IllegalArgumentException("Order is not currently assigned to a delivery boy.");
        }
        
        if (order.getCompletionToken() == null || !order.getCompletionToken().equals(token)) {
            throw new IllegalArgumentException("Invalid completion token for order.");
        }
        
        // Mark as delivered
        order.setDeliveryStatus("DELIVERED");
        // We could also set the order.status to "COMPLETED" here if appropriate
        order.setStatus("COMPLETED");

        orderRepository.save(order);
    }
}
