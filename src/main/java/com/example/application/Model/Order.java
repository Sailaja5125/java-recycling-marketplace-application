package com.example.application.Model;
import jakarta.persistence.*;


@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;
    private int quantity;
    private double price;

    /**
     * Id of the user who placed this order.
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private Long userId;

    /**
     * Id of the product that was purchased.
     * This lets us restore stock when an order is cancelled.
     */
    private Long productId;

    /**
     * Simple status field: e.g. "PLACED", "CANCELLED".
     */
    private String status = "PLACED";

    private Long assignedDeliveryBoyId;
    
    private String completionToken;
    
    private String deliveryStatus = "PENDING"; // PENDING, ASSIGNED, DELIVERED

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Long getAssignedDeliveryBoyId() {
        return assignedDeliveryBoyId;
    }

    public void setAssignedDeliveryBoyId(Long assignedDeliveryBoyId) {
        this.assignedDeliveryBoyId = assignedDeliveryBoyId;
    }

    public String getCompletionToken() {
        return completionToken;
    }

    public void setCompletionToken(String completionToken) {
        this.completionToken = completionToken;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }
}