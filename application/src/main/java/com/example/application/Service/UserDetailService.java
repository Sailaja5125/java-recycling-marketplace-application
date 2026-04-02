package com.example.application.Service;

import com.example.application.Model.User;
import com.example.application.Model.UserDetail;
import com.example.application.Repository.UserRepository;
import com.example.application.Repository.UserDetailRepository;
import com.example.application.Utils.UserDetailDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailService {

    private final UserDetailRepository userDetailRepository;
    private final UserRepository userRepository;

    public UserDetailService(UserDetailRepository userDetailRepository, UserRepository userRepository) {
        this.userDetailRepository = userDetailRepository;
        this.userRepository = userRepository;
    }

    public UserDetailDTO addDetails(UserDetail detail, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (detail.getPickupTime() == null) {
            detail.setPickupTime(new java.util.Date(System.currentTimeMillis() + 24L * 60 * 60 * 1000));
        }

        detail.setUser(user);
        detail.setStatus("PENDING");
        UserDetail saved = userDetailRepository.save(detail);

        return new UserDetailDTO(saved);
    }

    public UserDetailDTO modifyDetails(Long id, UserDetail updatedDetail) {
        UserDetail existing = userDetailRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Detail not found"));

        if (updatedDetail.getAddress() != null) existing.setAddress(updatedDetail.getAddress());
        if (updatedDetail.getMaterials() != null) existing.setMaterials(updatedDetail.getMaterials());
        if (updatedDetail.getPickupTime() != null) existing.setPickupTime(updatedDetail.getPickupTime());
        if (updatedDetail.getQuantity() != 0) existing.setQuantity(updatedDetail.getQuantity());

        return new UserDetailDTO(userDetailRepository.save(existing));
    }

    public List<UserDetailDTO> getDetailsByUser(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        return userDetailRepository.findByUserId(userId).stream()
                .map(UserDetailDTO::new)
                .toList();
    }

    public UserDetailDTO assignDeliveryBoy(Long detailId, Long deliveryBoyId) {
        UserDetail detail = userDetailRepository.findById(detailId)
                .orElseThrow(() -> new IllegalArgumentException("Detail not found"));
        User deliveryBoy = userRepository.findById(deliveryBoyId)
                .orElseThrow(() -> new IllegalArgumentException("Delivery Boy not found"));

        if (!com.example.application.Model.Role.DELIVERY_BOY.equals(deliveryBoy.getRole())) {
            throw new IllegalArgumentException("Assigned user is not a delivery boy");
        }

        detail.setAssignedDeliveryBoyId(deliveryBoyId);
        detail.setStatus("ASSIGNED");
        
        // Generate a 6-digit OTP token
        String token = String.format("%06d", new java.util.Random().nextInt(999999));
        detail.setCompletionToken(token);

        return new UserDetailDTO(userDetailRepository.save(detail));
    }

    public UserDetailDTO completePickup(Long detailId, String token) {
        UserDetail detail = userDetailRepository.findById(detailId)
                .orElseThrow(() -> new IllegalArgumentException("Detail not found"));

        if (!"ASSIGNED".equals(detail.getStatus())) {
            throw new IllegalArgumentException("Pickup is not assigned");
        }

        if (detail.getCompletionToken() == null || !detail.getCompletionToken().equals(token)) {
            throw new IllegalArgumentException("Invalid OTP token");
        }

        detail.setStatus("COMPLETED");
        // token can be nullified or left for records; we'll leave it
        UserDetail saved = userDetailRepository.save(detail);

        // Award points to the user
        User user = detail.getUser();
        int rewards = detail.getQuantity() * 50;
        user.setRewards(user.getRewards() + rewards);
        userRepository.save(user);

        return new UserDetailDTO(saved);
    }

    public List<UserDetailDTO> getAssignedPickups(Long deliveryBoyId) {
        return userDetailRepository.findByAssignedDeliveryBoyId(deliveryBoyId).stream()
                .map(UserDetailDTO::new)
                .toList();
    }

    public List<UserDetailDTO> getAllPendingPickups() {
        return userDetailRepository.findByStatus("PENDING").stream()
                .map(UserDetailDTO::new)
                .toList();
    }
}
