package com.example.application.Repository;

import com.example.application.Model.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface UserDetailRepository extends JpaRepository<UserDetail, Long> {
    List<UserDetail> findByUserId(Long userId);
    List<UserDetail> findByAssignedDeliveryBoyId(Long deliveryBoyId);
    List<UserDetail> findByStatus(String status);
    List<UserDetail> findByStatusOrStatusIsNull(String status);
}

