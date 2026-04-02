package com.example.application.Utils;

import com.example.application.Model.User;

// UserDTO.java
public class UserDTO {
    private Long id;
    private String email;
    private String name;
    private int rewards;
    private String role;

    public UserDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getUsername();
        this.rewards = user.getRewards();
        this.role = user.getRole() != null ? user.getRole().name() : "USER";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRewards() {
        return rewards;
    }

    public void setRewards(int rewards) {
        this.rewards = rewards;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}