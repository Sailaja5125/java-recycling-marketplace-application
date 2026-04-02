package com.example.application.Controller;

import com.example.application.Model.UserDetail;
import com.example.application.Service.UserDetailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/details")
public class UserDetailController {

    private final UserDetailService userDetailService;

    public UserDetailController(UserDetailService userDetailService) {
        this.userDetailService = userDetailService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addDetails(@RequestBody UserDetail detail, @RequestParam Long userId) {
        try {
            return ResponseEntity.status(201).body(userDetailService.addDetails(detail, userId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PutMapping("/modify/{id}")
    public ResponseEntity<?> modifyDetails(@PathVariable Long id, @RequestBody UserDetail updatedDetail) {
        try {
            return ResponseEntity.ok(userDetailService.modifyDetails(id, updatedDetail));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getDetailsByUser(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(userDetailService.getDetailsByUser(userId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PostMapping("/assign/{detailId}")
    public ResponseEntity<?> assignDeliveryBoy(@PathVariable Long detailId, @RequestParam Long deliveryBoyId) {
        try {
            return ResponseEntity.ok(userDetailService.assignDeliveryBoy(detailId, deliveryBoyId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PostMapping("/complete/{detailId}")
    public ResponseEntity<?> completePickup(@PathVariable Long detailId, @RequestParam String token) {
        try {
            return ResponseEntity.ok(userDetailService.completePickup(detailId, token));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @GetMapping("/delivery/{deliveryBoyId}")
    public ResponseEntity<?> getAssignedPickups(@PathVariable Long deliveryBoyId) {
        return ResponseEntity.ok(userDetailService.getAssignedPickups(deliveryBoyId));
    }

    @GetMapping("/pending")
    public ResponseEntity<?> getAllPendingPickups() {
        return ResponseEntity.ok(userDetailService.getAllPendingPickups());
    }
}