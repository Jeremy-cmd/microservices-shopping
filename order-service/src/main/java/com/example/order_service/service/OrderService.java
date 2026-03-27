package com.example.order_service.service;

import com.example.microservices.order.event.OrderPlacedEvent;
import com.example.order_service.client.InventoryClient;
import com.example.order_service.dto.OrderRequest;
import com.example.order_service.model.Order;
import com.example.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final static Logger log = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public void placeOrder(OrderRequest orderRequest) {
        var isProductInStock = inventoryClient.isInStock(orderRequest.skuCode(), orderRequest.quantity());

        if(isProductInStock) {
            Order order = new Order();
            order.setOrderNumber(UUID.randomUUID().toString());
            order.setPrice(orderRequest.price());
            order.setSkuCode(orderRequest.skuCode());
            order.setQuantity(orderRequest.quantity());

            orderRepository.save(order);

            var orderPlacedEvent = new OrderPlacedEvent(order.getOrderNumber(), orderRequest.userDetails()
                    .email(),
                    orderRequest.userDetails().firstName(),
                    orderRequest.userDetails().lastName());
            log.info("Start- Sending OrderPlacedEvent {} to Kafka Topic", orderPlacedEvent);
            kafkaTemplate.send("order-placed", orderPlacedEvent);
            log.info("End- Sending OrderPlacedEvent {} to Kafka Topic", orderPlacedEvent);
        }
    }


}
