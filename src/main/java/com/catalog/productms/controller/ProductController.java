package com.catalog.productms.controller;

import com.catalog.productms.dto.PageResponse;
import com.catalog.productms.dto.ProductRequest;
import com.catalog.productms.dto.ProductResponse;
import com.catalog.productms.entity.Product;
import com.catalog.productms.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product Catalog API")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @Operation(summary = "Create a new product", description = "Creates a new product in the catalog")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Product created successfully",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        Product product = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ProductResponse.fromEntity(product));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a product", description = "Updates an existing product by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product updated successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<ProductResponse> updateProduct(
            @Parameter(description = "Product ID") @PathVariable String id,
            @Valid @RequestBody ProductRequest request) {
        Product product = productService.updateProduct(id, request);
        return ResponseEntity.ok(ProductResponse.fromEntity(product));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieves a product by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product found"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ProductResponse> getProductById(
            @Parameter(description = "Product ID") @PathVariable String id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(ProductResponse.fromEntity(product));
    }

    @GetMapping
    @Operation(
        summary = "Get all products", 
        description = "Retrieves all products in the catalog. " +
                     "Returns a list by default (backward compatible). " +
                     "When pagination parameters (page or size) are provided, returns a paginated response with metadata."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Products retrieved successfully. " +
                    "Returns List<ProductResponse> without pagination params, " +
                    "or PageResponse<ProductResponse> with pagination params.")
    })
    public ResponseEntity<?> getAllProducts(
            @Parameter(
                description = "Page number (0-based). If provided, response will be paginated.", 
                example = "0"
            ) 
            @RequestParam(required = false) Integer page,
            @Parameter(
                description = "Page size (default: 20, max: 100). If provided, response will be paginated.", 
                example = "10"
            ) 
            @RequestParam(required = false) Integer size) {
        
        // If pagination parameters provided, return paginated response
        if (page != null || size != null) {
            int pageNumber = page != null ? page : 0;
            int pageSize = size != null ? Math.min(size, 100) : 20; // Max 100 items per page
            
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            
            Page<Product> productPage = productService.getAllProducts(pageable);
            Page<ProductResponse> responsePage = productPage.map(ProductResponse::fromEntity);
            
            return ResponseEntity.ok(PageResponse.fromPage(responsePage));
        }
        
        // Default behavior: return all products (backward compatibility)
        List<Product> products = productService.getAllProducts();
        List<ProductResponse> response = products.stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(
        summary = "Search products", 
        description = "Search and filter products by text query (name/description), price range, with optional pagination. " +
                     "All parameters are optional. Returns matching products as a list or paginated response."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search results retrieved successfully. " +
                    "Returns List<ProductResponse> without pagination params, " +
                    "or PageResponse<ProductResponse> with pagination params.")
    })
    public ResponseEntity<?> searchProducts(
            @Parameter(
                description = "Search query - matches products where name or description contains this text (case-insensitive)", 
                example = "laptop"
            ) 
            @RequestParam(required = false) String q,
            @Parameter(
                description = "Minimum price filter (inclusive) - only products with price >= this value", 
                example = "100.00"
            ) 
            @RequestParam(name = "min_price", required = false) BigDecimal minPrice,
            @Parameter(
                description = "Maximum price filter (inclusive) - only products with price <= this value", 
                example = "2000.00"
            ) 
            @RequestParam(name = "max_price", required = false) BigDecimal maxPrice,
            @Parameter(
                description = "Page number (0-based). If provided, response will be paginated.", 
                example = "0"
            ) 
            @RequestParam(required = false) Integer page,
            @Parameter(
                description = "Page size (default: 20, max: 100). If provided, response will be paginated.", 
                example = "5"
            ) 
            @RequestParam(required = false) Integer size) {
        
        // If pagination parameters provided, return paginated response
        if (page != null || size != null) {
            int pageNumber = page != null ? page : 0;
            int pageSize = size != null ? Math.min(size, 100) : 20; // Max 100 items per page
            
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            
            Page<Product> productPage = productService.searchProducts(q, minPrice, maxPrice, pageable);
            Page<ProductResponse> responsePage = productPage.map(ProductResponse::fromEntity);
            
            return ResponseEntity.ok(PageResponse.fromPage(responsePage));
        }
        
        // Default behavior: return all matching products (backward compatibility)
        List<Product> products = productService.searchProducts(q, minPrice, maxPrice);
        List<ProductResponse> response = products.stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product", description = "Deletes a product by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product ID") @PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    @Operation(summary = "Delete all products", description = "Deletes all products from the catalog")
    @ApiResponse(responseCode = "204", description = "All products deleted successfully")
    public ResponseEntity<Void> deleteAllProducts() {
        productService.deleteAllProducts();
        return ResponseEntity.noContent().build();
    }
}

