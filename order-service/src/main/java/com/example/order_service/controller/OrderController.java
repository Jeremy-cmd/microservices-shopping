package com.example.order_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

   // private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String placeOrder() {
      //  orderService.placeOrder(orderRequest);
        return "Order Placed Successfully";
    }
}
