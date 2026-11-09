package com.example.order_service.exception;

public class OutOfStockException extends RuntimeException {

    public OutOfStockException(String skuCode, Integer quantity) {
        super(String.format("Product with skuCode %s is not in stock for quantity %d", skuCode, quantity));
    }
}
