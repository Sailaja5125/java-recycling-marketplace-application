package com.example.application.Utils;


import com.example.application.Model.UserDetail;

import java.util.Date;
import java.util.List;

public class UserDetailDTO {
    private Long id;
    private List<String> materials;
    private Date pickupTime;
    private String address;
    private int quantity;
    private Long userId;
    private Long assignedDeliveryBoyId;
    private String status;
    private String completionToken;

    public UserDetailDTO(UserDetail detail) {
        this.id = detail.getId();
        this.materials = detail.getMaterials();
        this.pickupTime = detail.getPickupTime();
        this.address = detail.getAddress();
        this.quantity = detail.getQuantity();
        this.userId = detail.getUser().getId();
        this.assignedDeliveryBoyId = detail.getAssignedDeliveryBoyId();
        this.status = detail.getStatus();
        this.completionToken = detail.getCompletionToken();
    }


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<String> getMaterials() {
        return materials;
    }

    public void setMaterials(List<String> materials) {
        this.materials = materials;
    }

    public Date getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(Date pickupTime) {
        this.pickupTime = pickupTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Long getAssignedDeliveryBoyId() {
        return assignedDeliveryBoyId;
    }

    public void setAssignedDeliveryBoyId(Long assignedDeliveryBoyId) {
        this.assignedDeliveryBoyId = assignedDeliveryBoyId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCompletionToken() {
        return completionToken;
    }

    public void setCompletionToken(String completionToken) {
        this.completionToken = completionToken;
    }
}