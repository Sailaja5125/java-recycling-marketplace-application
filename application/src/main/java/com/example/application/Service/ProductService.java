package com.example.application.Service;

import com.example.application.Model.Product;
import com.example.application.Repository.ProductRepository;
import com.example.application.Repository.SellerRepository;
import com.example.application.Utils.ProductWithSellerDTO;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final SellerRepository sellerRepository;
    private final Cloudinary cloudinary;

    public ProductService(ProductRepository productRepository, SellerRepository sellerRepository, Cloudinary cloudinary) {
        this.productRepository = productRepository;
        this.sellerRepository = sellerRepository;
        this.cloudinary = cloudinary;
    }

    public String uploadImage(MultipartFile file) throws IOException {
        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return uploadResult.get("url").toString();
    }

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    public Optional<Product> getProduct(Long id) {
        return productRepository.findById(id);
    }

    public Optional<Product> updateProduct(Long id, Product updatedProduct) {
        return productRepository.findById(id).map(existing -> {
            if (updatedProduct.getProductName() != null) existing.setProductName(updatedProduct.getProductName());
            if (updatedProduct.getProductDetails() != null) existing.setProductDetails(updatedProduct.getProductDetails());
            if (updatedProduct.getSellerId() != null) existing.setSellerId(updatedProduct.getSellerId());
            if (updatedProduct.getQuantity() != 0) existing.setQuantity(updatedProduct.getQuantity());
            if (updatedProduct.getPrice() != 0.0) existing.setPrice(updatedProduct.getPrice());
            return productRepository.save(existing);
        });
    }

    public boolean deleteProduct(Long id) {
        if (!productRepository.existsById(id)) return false;
        productRepository.deleteById(id);
        return true;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getProductsBySeller(String sellerId) {
        return productRepository.findBySellerId(sellerId);
    }

    public List<ProductWithSellerDTO> getAllProductsWithSellers() {
        return productRepository.findAll().stream()
                .map(product -> sellerRepository.findById(Long.valueOf(product.getSellerId()))
                        .map(seller -> new ProductWithSellerDTO(product, seller))
                        .orElse(null))
                .filter(dto -> dto != null)
                .toList();
    }
}
