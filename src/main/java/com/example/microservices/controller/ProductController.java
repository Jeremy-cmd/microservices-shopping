package com.example.microservices.controller;

import com.example.microservices.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {


   // private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto createProduct(@RequestBody ProductDto productRequest) {
        //return productService.createProduct(productRequest);
        return null;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductDto> getAllProducts() {
        //return productService.getAllProducts();
        return null;
    }
}
