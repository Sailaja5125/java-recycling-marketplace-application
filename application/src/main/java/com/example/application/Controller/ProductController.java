package com.example.application.Controller;

import com.example.application.Model.Product;
import com.example.application.Service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/products")

public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Add new product with image upload
    @PostMapping("/add")
    public ResponseEntity<?> addProduct(
            @RequestParam("productName") String productName,
            @RequestParam("productDetails") String productDetails,
            @RequestParam("price") double price,
            @RequestParam("quantity") int quantity,
            @RequestParam("sellerId") String sellerId,
            @RequestParam("image") MultipartFile image) {
        try {
            String imageUrl = productService.uploadImage(image);
            
            Product product = new Product();
            product.setProductName(productName);
            product.setProductDetails(productDetails);
            product.setPrice(price);
            product.setQuantity(quantity);
            product.setSellerId(sellerId);
            product.setImageUrl(imageUrl);
            
            return ResponseEntity.status(201).body(productService.addProduct(product));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to add product: " + e.getMessage());
        }
    }

    // Get product by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Long id) {
        return productService.getProduct(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body("Product not found"));
    }

    // Update product
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product updatedProduct) {
        return productService.updateProduct(id, updatedProduct)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body("Product not found"));
    }

    // Delete product
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        if (productService.deleteProduct(id)) {
            return ResponseEntity.ok("Product deleted successfully");
        }
        return ResponseEntity.status(404).body("Product not found");
    }

    // List all products
    @GetMapping("/all")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // Get all products by sellerId
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<Product>> getProductsBySeller(@PathVariable String sellerId) {
        return ResponseEntity.ok(productService.getProductsBySeller(sellerId));
    }

    @GetMapping("/allWithSellers")
    public ResponseEntity<?> getAllProductsWithSellers() {
        return ResponseEntity.ok(productService.getAllProductsWithSellers());
    }
}