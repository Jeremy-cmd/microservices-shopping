package com.example.microservices.product_service.service;

import com.example.microservices.product_service.dto.ProductDto;
import com.example.microservices.product_service.model.Product;
import com.example.microservices.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public ProductDto createProduct(ProductDto productDto) {
        Product product = Product.builder()
                .name(productDto.name())
                .description(productDto.description())
                .skuCode(productDto.skuCode())
                .price(productDto.price())
                .build();
        productRepository.save(product);
        log.info("Product created successfully");
        return new ProductDto(product.getId(), product.getName(),
                product.getDescription(),
                product.getSkuCode(), product.getPrice());
    }

    public List<ProductDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(product -> new ProductDto(product.getId(), product.getName(), product.getDescription(),
                        product.getSkuCode(),
                        product.getPrice()))
                .toList();
    }


}
