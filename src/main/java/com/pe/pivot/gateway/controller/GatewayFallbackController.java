package com.pe.pivot.gateway.controller;

import com.pe.pivot.gateway.controller.dto.FallbackResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Reactive Fallback Controller triggered by Resilience4j when a downstream microservice
 * exceeds the 2-second timeout or is unavailable. This controller returns a 503 Service Unavailable response to the client.
 * It is important to note that this controller is not used for handling exceptions thrown by the microservices themselves,
 * but rather for handling scenarios where the microservices are unresponsive or slow to respond.
 */
@RestController
@RequestMapping("/fallback")
public class GatewayFallbackController {

  /**
   * Handles fallback requests for a specific downstream microservice.
   * When a request to a downstream service fails due to timeout or unavailability,
   *
   * @param service - The name of the downstream microservice that triggered the fallback.
   * @param exchange - The ServerWebExchange object that provides access to the HTTP request and response.
   * @return - A Mono containing a FallbackResponse object with details about the fallback event.
   */
  @RequestMapping("/{service}")
  @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
  public Mono<FallbackResponse> fallback(@PathVariable String service, ServerWebExchange exchange) {
    FallbackResponse response = new FallbackResponse(
            Instant.now(),
            HttpStatus.SERVICE_UNAVAILABLE.value(),
            "Service Unavailable (Circuit Breaker Triggered)",
            "Downstream microservice [" + service + "] did not respond within the 2-second timeout or is currently unavailable.",
            exchange.getRequest().getPath().value()
    );
    return Mono.just(response);
  }


}