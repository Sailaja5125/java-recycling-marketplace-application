package com.example.application.Utils;

import com.example.application.Model.Product;
import com.example.application.Model.Seller;

public class ProductWithSellerDTO {
    private Long productId;
    private String productName;
    private String productDetails;
    private int quantity;
    private double price;
    private String imageUrl;

    private Long sellerId;
    private String companyName;
    private String location;
    private String mobileNumber;
    private String email;

    public ProductWithSellerDTO(Product product, Seller seller) {
        this.productId = product.getId();
        this.productName = product.getProductName();
        this.productDetails = product.getProductDetails();
        this.quantity = product.getQuantity();
        this.price = product.getPrice();
        this.imageUrl = product.getImageUrl();

        this.sellerId = seller.getId();
        this.companyName = seller.getCompanyName();
        this.location = seller.getLocation();
        this.mobileNumber = seller.getMobileNumber();
        this.email = seller.getEmail();
    }

    // getters

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDetails() {
        return productDetails;
    }

    public void setProductDetails(String productDetails) {
        this.productDetails = productDetails;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}