package com.catalog.productms.controller;

import com.catalog.productms.dto.ProductRequest;
import com.catalog.productms.entity.Product;
import com.catalog.productms.exception.ProductAlreadyExistsException;
import com.catalog.productms.exception.ProductNotFoundException;
import com.catalog.productms.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
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
    void createProduct_WithValidRequest_ShouldReturn201() throws Exception {
        when(productService.createProduct(any(ProductRequest.class))).thenReturn(product);

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("123"))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.price").value(99.99));

        verify(productService, times(1)).createProduct(any(ProductRequest.class));
    }

    @Test
    void createProduct_WithDuplicateNameAndDescription_ShouldReturn400() throws Exception {
        when(productService.createProduct(any(ProductRequest.class)))
                .thenThrow(new ProductAlreadyExistsException("Product already exists"));

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status_code").value(400))
                .andExpect(jsonPath("$.message").value("Product already exists"));

        verify(productService, times(1)).createProduct(any(ProductRequest.class));
    }

    @Test
    void createProduct_WithInvalidRequest_ShouldReturn400() throws Exception {
        ProductRequest invalidRequest = new ProductRequest();
        invalidRequest.setName("");
        invalidRequest.setDescription("");
        invalidRequest.setPrice(new BigDecimal("-10"));

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status_code").value(400))
                .andExpect(jsonPath("$.message").exists());

        verify(productService, never()).createProduct(any(ProductRequest.class));
    }

    @Test
    void createProduct_WithMissingFields_ShouldReturn400() throws Exception {
        ProductRequest invalidRequest = new ProductRequest();

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status_code").value(400));

        verify(productService, never()).createProduct(any(ProductRequest.class));
    }

    @Test
    void updateProduct_WithValidRequest_ShouldReturn200() throws Exception {
        when(productService.updateProduct(eq("123"), any(ProductRequest.class))).thenReturn(product);

        mockMvc.perform(put("/products/123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("123"))
                .andExpect(jsonPath("$.name").value("Test Product"));

        verify(productService, times(1)).updateProduct(eq("123"), any(ProductRequest.class));
    }

    @Test
    void updateProduct_WhenProductNotFound_ShouldReturn404() throws Exception {
        when(productService.updateProduct(eq("999"), any(ProductRequest.class)))
                .thenThrow(new ProductNotFoundException("999"));

        mockMvc.perform(put("/products/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).updateProduct(eq("999"), any(ProductRequest.class));
    }

    @Test
    void updateProduct_WithInvalidRequest_ShouldReturn400() throws Exception {
        ProductRequest invalidRequest = new ProductRequest();
        invalidRequest.setName("");

        mockMvc.perform(put("/products/123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(productService, never()).updateProduct(any(), any());
    }

    @Test
    void getProductById_WhenProductExists_ShouldReturn200() throws Exception {
        when(productService.getProductById("123")).thenReturn(product);

        mockMvc.perform(get("/products/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("123"))
                .andExpect(jsonPath("$.name").value("Test Product"));

        verify(productService, times(1)).getProductById("123");
    }

    @Test
    void getProductById_WhenProductNotFound_ShouldReturn404() throws Exception {
        when(productService.getProductById("999")).thenThrow(new ProductNotFoundException("999"));

        mockMvc.perform(get("/products/999"))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).getProductById("999");
    }

    @Test
    void getAllProducts_ShouldReturn200WithList() throws Exception {
        Product product2 = new Product();
        product2.setId("456");
        product2.setName("Product 2");
        product2.setDescription("Description 2");
        product2.setPrice(new BigDecimal("49.99"));

        List<Product> products = Arrays.asList(product, product2);
        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("123"))
                .andExpect(jsonPath("$[1].id").value("456"));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void getAllProducts_WhenEmpty_ShouldReturn200WithEmptyList() throws Exception {
        when(productService.getAllProducts()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void searchProducts_WithAllParameters_ShouldReturn200() throws Exception {
        List<Product> products = Arrays.asList(product);
        when(productService.searchProducts(eq("Test"), eq(new BigDecimal("50.00")), eq(new BigDecimal("150.00"))))
                .thenReturn(products);

        mockMvc.perform(get("/products/search")
                .param("q", "Test")
                .param("min_price", "50.00")
                .param("max_price", "150.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("123"));

        verify(productService, times(1)).searchProducts("Test", new BigDecimal("50.00"), new BigDecimal("150.00"));
    }

    @Test
    void searchProducts_WithNoParameters_ShouldReturn200() throws Exception {
        List<Product> products = Arrays.asList(product);
        when(productService.searchProducts(null, null, null)).thenReturn(products);

        mockMvc.perform(get("/products/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(productService, times(1)).searchProducts(null, null, null);
    }

    @Test
    void searchProducts_WhenEmpty_ShouldReturn200WithEmptyList() throws Exception {
        when(productService.searchProducts(any(), any(), any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/products/search")
                .param("q", "nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(productService, times(1)).searchProducts(eq("nonexistent"), eq(null), eq(null));
    }

    @Test
    void deleteProduct_WhenProductExists_ShouldReturn200() throws Exception {
        doNothing().when(productService).deleteProduct("123");

        mockMvc.perform(delete("/products/123"))
                .andExpect(status().isOk());

        verify(productService, times(1)).deleteProduct("123");
    }

    @Test
    void deleteProduct_WhenProductNotFound_ShouldReturn404() throws Exception {
        doThrow(new ProductNotFoundException("999")).when(productService).deleteProduct("999");

        mockMvc.perform(delete("/products/999"))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).deleteProduct("999");
    }

    @Test
    void deleteAllProducts_ShouldReturn204() throws Exception {
        doNothing().when(productService).deleteAllProducts();

        mockMvc.perform(delete("/products"))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteAllProducts();
    }
}

