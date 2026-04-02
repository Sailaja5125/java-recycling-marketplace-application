package com.example.application.Service;

import com.example.application.Model.Seller;
import com.example.application.Repository.SellerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SellerService {

    private final SellerRepository sellerRepository;

    public SellerService(SellerRepository sellerRepository) {
        this.sellerRepository = sellerRepository;
    }

    public Seller addSeller(Seller seller) {
        return sellerRepository.save(seller);
    }

    public Optional<Seller> getSeller(Long id) {
        return sellerRepository.findById(id);
    }

    public Optional<Seller> updateSeller(Long id, Seller updatedSeller) {
        return sellerRepository.findById(id).map(existing -> {
            if (updatedSeller.getCompanyName() != null) existing.setCompanyName(updatedSeller.getCompanyName());
            if (updatedSeller.getLocation() != null) existing.setLocation(updatedSeller.getLocation());
            if (updatedSeller.getMobileNumber() != null) existing.setMobileNumber(updatedSeller.getMobileNumber());
            if (updatedSeller.getEmail() != null) existing.setEmail(updatedSeller.getEmail());
            if (updatedSeller.getGstNumber() != null) existing.setGstNumber(updatedSeller.getGstNumber());
            if (updatedSeller.getProductIds() != null) existing.setProductIds(updatedSeller.getProductIds());
            if (updatedSeller.getCustomerIds() != null) existing.setCustomerIds(updatedSeller.getCustomerIds());
            return sellerRepository.save(existing);
        });
    }

    public List<Seller> getAllSellers() {
        return sellerRepository.findAll();
    }

    public Optional<Long> getSellerIdByEmail(String email) {
        return sellerRepository.findByEmail(email).map(Seller::getId);
    }
}
