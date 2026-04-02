package com.example.application.Model;
import jakarta.persistence.*;


import java.util.Date;
import java.util.List;

@Entity
@Table(name = "user_details")
public class UserDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ElementCollection
    @CollectionTable(name = "detail_materials", joinColumns = @JoinColumn(name = "detail_id"))
    @Column(name = "material")
    private List<String> materials;

    
    private Date pickupTime;

    @Column
    private String address;

    @Column
    private int quantity=0;

    private Long assignedDeliveryBoyId;

    private String status = "PENDING"; // PENDING, ASSIGNED, COMPLETED

    private String completionToken;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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



    // getters and setters
}