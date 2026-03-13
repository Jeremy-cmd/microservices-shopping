package com.example.microservices.product_service.dto;

import java.math.BigDecimal;

public record ProductDto(String id, String name, String description, String skuCode, BigDecimal price) {
}
