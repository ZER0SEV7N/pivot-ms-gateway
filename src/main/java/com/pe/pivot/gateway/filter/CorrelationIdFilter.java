package com.pe.pivot.gateway.filter;

import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Reactive Global Filter that ensures every request routed through the API Gateway carries a
 * unique X-Correlation-Id header for distributed banking observability in Loki and Grafana.
 */
@Slf4j
@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {

  // The name of the header used to carry the correlation ID
  public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

  /**
   * Filters incoming requests to ensure they have a correlation ID. If the request does not
   *
   * @param exchange the current server exchange
   * @param chain provides a way to delegate to the next filter
   * @return - a Mono that indicates when request processing is complete
   */
  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    // Retrieve the correlation ID from the request headers, or generate a new one if not present
    String correlationId =
            Optional.ofNullable(
                            exchange.getRequest().getHeaders().getFirst(CORRELATION_ID_HEADER))
                    .filter(id -> !id.isBlank())
                    .orElseGet(() -> UUID.randomUUID().toString());

    // Mutate the request to include the correlation ID header
    ServerHttpRequest mutatedRequest =
            exchange.getRequest().mutate().header(CORRELATION_ID_HEADER, correlationId).build();

    // Mutate the response to include the correlation ID header
    exchange.getResponse().getHeaders().set(CORRELATION_ID_HEADER, correlationId);

    // Log the routing information for debugging purposes
    org.slf4j.MDC.put(CORRELATION_ID_HEADER, correlationId);
    log.debug(
            "Routing request [{} {}] with {}={}",
            mutatedRequest.getMethod(),
            mutatedRequest.getURI().getPath(),
            CORRELATION_ID_HEADER,
            correlationId);
    org.slf4j.MDC.remove(CORRELATION_ID_HEADER);

    return chain.filter(exchange.mutate().request(mutatedRequest).build());
  }

  /**
   * Returns the order of this filter in the list of global filters.
   *
   * @return - the order of this filter, with lower values indicating higher precedence
   */
  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE;
  }
}