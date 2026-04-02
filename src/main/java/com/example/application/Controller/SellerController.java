package com.example.application.Controller;

import com.example.application.Model.Seller;
import com.example.application.Service.SellerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seller")
public class SellerController {

    private final SellerService sellerService;

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addSeller(@RequestBody Seller seller) {
        return ResponseEntity.status(201).body(sellerService.addSeller(seller));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSeller(@PathVariable Long id) {
        return sellerService.getSeller(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body("Seller not found"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateSeller(@PathVariable Long id, @RequestBody Seller updatedSeller) {
        return sellerService.updateSeller(id, updatedSeller)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body("Seller not found"));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Seller>> getAllSellers() {
        return ResponseEntity.ok(sellerService.getAllSellers());
    }

    @GetMapping("/idByEmail")
    public ResponseEntity<?> getSellerIdByEmail(@RequestParam String email) {
        return sellerService.getSellerIdByEmail(email)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body("Seller not found"));
    }
}
