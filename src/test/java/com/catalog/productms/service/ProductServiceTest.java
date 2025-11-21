package com.catalog.productms.service;

import com.catalog.productms.dto.ProductRequest;
import com.catalog.productms.entity.Product;
import com.catalog.productms.exception.ProductAlreadyExistsException;
import com.catalog.productms.exception.ProductNotFoundException;
import com.catalog.productms.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId("123");
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(new BigDecimal("99.99"));

        productRequest = new ProductRequest();
        productRequest.setName("Test Product");
        productRequest.setDescription("Test Description");
        productRequest.setPrice(new BigDecimal("99.99"));
    }

    @Test
    void createProduct_ShouldReturnCreatedProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.createProduct(productRequest);

        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertEquals(new BigDecimal("99.99"), result.getPrice());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void updateProduct_WhenProductExists_ShouldReturnUpdatedProduct() {
        ProductRequest updateRequest = new ProductRequest();
        updateRequest.setName("Updated Product");
        updateRequest.setDescription("Updated Description");
        updateRequest.setPrice(new BigDecimal("149.99"));

        Product updatedProduct = new Product();
        updatedProduct.setId("123");
        updatedProduct.setName("Updated Product");
        updatedProduct.setDescription("Updated Description");
        updatedProduct.setPrice(new BigDecimal("149.99"));

        when(productRepository.findById("123")).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        Product result = productService.updateProduct("123", updateRequest);

        assertNotNull(result);
        assertEquals("Updated Product", result.getName());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(new BigDecimal("149.99"), result.getPrice());
        verify(productRepository, times(1)).findById("123");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void updateProduct_WhenProductNotFound_ShouldThrowException() {
        when(productRepository.findById("999")).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> {
            productService.updateProduct("999", productRequest);
        });

        verify(productRepository, times(1)).findById("999");
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void getProductById_WhenProductExists_ShouldReturnProduct() {
        when(productRepository.findById("123")).thenReturn(Optional.of(product));

        Product result = productService.getProductById("123");

        assertNotNull(result);
        assertEquals("123", result.getId());
        assertEquals("Test Product", result.getName());
        verify(productRepository, times(1)).findById("123");
    }

    @Test
    void getProductById_WhenProductNotFound_ShouldThrowException() {
        when(productRepository.findById("999")).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> {
            productService.getProductById("999");
        });

        verify(productRepository, times(1)).findById("999");
    }

    @Test
    void getAllProducts_ShouldReturnListOfProducts() {
        Product product2 = new Product();
        product2.setId("456");
        product2.setName("Product 2");
        product2.setDescription("Description 2");
        product2.setPrice(new BigDecimal("49.99"));

        List<Product> products = Arrays.asList(product, product2);
        when(productRepository.findAll()).thenReturn(products);

        List<Product> result = productService.getAllProducts();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void searchProducts_WithAllParameters_ShouldReturnFilteredProducts() {
        List<Product> products = Arrays.asList(product);
        when(productRepository.searchProducts("Test", new BigDecimal("50.00"), new BigDecimal("150.00")))
                .thenReturn(products);

        List<Product> result = productService.searchProducts("Test", new BigDecimal("50.00"), new BigDecimal("150.00"));

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository, times(1)).searchProducts("Test", new BigDecimal("50.00"), new BigDecimal("150.00"));
    }

    @Test
    void searchProducts_WithNoParameters_ShouldReturnAllProducts() {
        List<Product> products = Arrays.asList(product);
        when(productRepository.searchProducts(null, null, null)).thenReturn(products);

        List<Product> result = productService.searchProducts(null, null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository, times(1)).searchProducts(null, null, null);
    }

    @Test
    void deleteProduct_WhenProductExists_ShouldDeleteProduct() {
        when(productRepository.existsById("123")).thenReturn(true);
        doNothing().when(productRepository).deleteById("123");

        productService.deleteProduct("123");

        verify(productRepository, times(1)).existsById("123");
        verify(productRepository, times(1)).deleteById("123");
    }

    @Test
    void deleteProduct_WhenProductNotFound_ShouldThrowException() {
        when(productRepository.existsById("999")).thenReturn(false);

        assertThrows(ProductNotFoundException.class, () -> {
            productService.deleteProduct("999");
        });

        verify(productRepository, times(1)).existsById("999");
        verify(productRepository, never()).deleteById(any());
    }
}

