package com.example.order_service;

import com.example.microservices.order.event.OrderPlacedEvent;
import com.example.order_service.repository.OrderRepository;
import com.example.order_service.stubs.InventoryClientStub;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableWireMock(@ConfigureWireMock(baseUrlProperties = "inventory.url"))
@Testcontainers
class OrderServiceApplicationTests {

	@Container
	@ServiceConnection
	static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.3.0");

	@LocalServerPort
	private Integer port;

	@Autowired
	private OrderRepository orderRepository;

	@MockitoBean
	private KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

	@BeforeEach
	void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
		orderRepository.deleteAll();
	}

	private static String orderJson(String skuCode, int quantity) {
		return """
				{
					"skuCode": "%s",
					"price": 1000,
					"quantity": %d,
					"userDetails": {
						"email": "jer@mail.com",
						"firstName": "Jer",
						"lastName": "Gon"
					}
				}
				""".formatted(skuCode, quantity);
	}

	@Test
	void shouldSubmitOrder() {
		InventoryClientStub.stubInventoryCall("iphone_15", 1);

		RestAssured.given()
				.contentType("application/json")
				.body(orderJson("iphone_15", 1))
				.when()
				.post("/api/order")
				.then()
				.log().all()
				.statusCode(202)
				.body(equalTo("Order Placed Successfully"));

		assertEquals(1, orderRepository.count());
		verify(kafkaTemplate).send(eq("order-placed"), any(OrderPlacedEvent.class));
	}

	@Test
	void shouldRejectOrderWhenOutOfStock() {
		InventoryClientStub.stubInventoryResponse("iphone_15", 5, false);

		RestAssured.given()
				.contentType("application/json")
				.body(orderJson("iphone_15", 5))
				.when()
				.post("/api/order")
				.then()
				.log().all()
				.statusCode(409)
				.body(containsString("not in stock"));

		assertEquals(0, orderRepository.count());
		verify(kafkaTemplate, never()).send(anyString(), any(OrderPlacedEvent.class));
	}

}
