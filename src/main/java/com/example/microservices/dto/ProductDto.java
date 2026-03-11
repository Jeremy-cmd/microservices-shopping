package com.example.microservices.dto;

import java.math.BigDecimal;

public record ProductDto(String id, String name, String description, BigDecimal price, String skuCode) {
}
