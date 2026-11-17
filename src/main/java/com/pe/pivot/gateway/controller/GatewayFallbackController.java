package com.pe.pivot.gateway.controller;

import com.pe.pivot.gateway.controller.dto.FallbackResponse;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Reactive fallback controller triggered by Resilience4j when a downstream microservice exceeds
 * the 2-second timeout or is unavailable.
 *
 * <p>This controller returns a 503 Service Unavailable response to the client. It is not used for
 * exceptions thrown by the microservices themselves, but for scenarios where the microservices are
 * unresponsive or slow to respond.
 */
@RestController
@RequestMapping("/fallback")
public class GatewayFallbackController {

  /**
   * Handles fallback requests for a specific downstream microservice.
   *
   * @param service the name of the downstream microservice that triggered the fallback
   * @param exchange the exchange that provides access to the HTTP request and response
   * @return a {@link Mono} containing the fallback response details
   */
  @RequestMapping(
      value = "/{service}",
      method = {
          RequestMethod.GET,
          RequestMethod.POST,
          RequestMethod.PUT,
          RequestMethod.PATCH,
          RequestMethod.DELETE
      })
  @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
  public Mono<FallbackResponse> fallback(@PathVariable String service,
      ServerWebExchange exchange) {

    // Create a FallbackResponse object with the current timestamp,
    // HTTP status code,
    // the request path, and a message indicating that the downstream microservice is unavailable.
    FallbackResponse response = new FallbackResponse(
        Instant.now(),
        HttpStatus.SERVICE_UNAVAILABLE.value(),
        "Service Unavailable (Circuit Breaker Triggered)",
        "Downstream microservice [" + service
            + "] did not respond within the 2-second timeout or is currently unavailable.",
        exchange.getRequest().getPath().value());
    return Mono.just(response);
  }
}
