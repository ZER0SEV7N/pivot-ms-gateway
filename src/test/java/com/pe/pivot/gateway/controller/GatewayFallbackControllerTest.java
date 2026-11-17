package com.pe.pivot.gateway.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.test.StepVerifier;

/**
 * Unit test suite for GatewayFallbackController.
 * Validates reactive fallback responses when Resilience4j Circuit Breaker triggers.
 */
class GatewayFallbackControllerTest {

  private GatewayFallbackController controller;

  @BeforeEach
  void setUp() {
    controller = new GatewayFallbackController();
  }

  @Test
  @DisplayName("Should return 503 Service Unavailable fallback response with service details")
  void shouldReturnServiceUnavailableFallbackResponse() {
    // Arrange
    String targetService = "ms-account";
    MockServerHttpRequest request =
            MockServerHttpRequest.post("/api/v1/accounts").build();
    MockServerWebExchange exchange = MockServerWebExchange.from(request);

    // Act
    var resultMono = controller.fallback(targetService, exchange);

    // Assert
    StepVerifier.create(resultMono)
            .assertNext(
                    response -> {
                      assertNotNull(response.timestamp());
                      assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), response.status());
                      assertEquals("Service Unavailable (Circuit Breaker Triggered)",
                              response.error());
                      assertTrue(response.message().contains(targetService));
                      assertEquals("/api/v1/accounts", response.path());
                    })
            .verifyComplete();
  }
}