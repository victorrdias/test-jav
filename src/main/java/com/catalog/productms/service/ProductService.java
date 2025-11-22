package com.catalog.productms.service;

import com.catalog.productms.dto.ProductRequest;
import com.catalog.productms.entity.Product;
import com.catalog.productms.exception.ProductAlreadyExistsException;
import com.catalog.productms.exception.ProductNotFoundException;
import com.catalog.productms.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public Product createProduct(ProductRequest request) {
        // Check for duplicate product with same name and description
        if (productRepository.existsByNameAndDescription(request.getName(), request.getDescription())) {
            throw new ProductAlreadyExistsException(
                "Product with name '" + request.getName() + "' and description '" + request.getDescription() + "' already exists"
            );
        }
        
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(String id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        
        return productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public Product getProductById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Product> searchProducts(String q, BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.searchProducts(q, minPrice, maxPrice);
    }

    @Transactional(readOnly = true)
    public Page<Product> searchProducts(String q, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productRepository.searchProducts(q, minPrice, maxPrice, pageable);
    }

    @Transactional
    public void deleteProduct(String id) {
        // Optimized: Single DB call instead of existsById + deleteById
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        productRepository.delete(product);
    }

    @Transactional
    public void deleteAllProducts() {
        productRepository.deleteAll();
    }
}

