package com.pe.pivot.gateway.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Unit test suite for CorrelationIdFilter.
 * Validates generation and propagation of X-Correlation-Id header across reactive chains.
 */
class CorrelationIdFilterTest {

  private CorrelationIdFilter filter;
  private GatewayFilterChain chain;

  @BeforeEach
  void setUp() {
    filter = new CorrelationIdFilter();
    chain = mock(GatewayFilterChain.class);
    when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
  }

  @Test
  @DisplayName("Should generate a new X-Correlation-Id when request header is missing")
  void shouldGenerateCorrelationIdWhenMissing() {
    // Arrange
    MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/customers").build();
    MockServerWebExchange exchange = MockServerWebExchange.from(request);
    ArgumentCaptor<ServerWebExchange> captor = ArgumentCaptor.forClass(ServerWebExchange.class);

    // Act & Assert
    StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

    verify(chain).filter(captor.capture());
    String generatedId =
            captor
                    .getValue()
                    .getRequest()
                    .getHeaders()
                    .getFirst(CorrelationIdFilter.CORRELATION_ID_HEADER);
    assertNotNull(generatedId);
    assertEquals(
            generatedId,
            exchange
                    .getResponse()
                    .getHeaders()
                    .getFirst(CorrelationIdFilter.CORRELATION_ID_HEADER));
    assertEquals(Ordered.HIGHEST_PRECEDENCE, filter.getOrder());
  }

  @Test
  @DisplayName("Should preserve existing X-Correlation-Id when provided by client")
  void shouldPreserveExistingCorrelationIdWhenPresent() {
    // Arrange
    String existingId = "corr-pivot-998877";
    MockServerHttpRequest request =
            MockServerHttpRequest.get("/api/v1/accounts")
                    .header(CorrelationIdFilter.CORRELATION_ID_HEADER, existingId)
                    .build();
    MockServerWebExchange exchange = MockServerWebExchange.from(request);
    ArgumentCaptor<ServerWebExchange> captor = ArgumentCaptor.forClass(ServerWebExchange.class);

    // Act & Assert
    StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

    verify(chain).filter(captor.capture());
    assertEquals(
            existingId,
            captor
                    .getValue()
                    .getRequest()
                    .getHeaders()
                    .getFirst(CorrelationIdFilter.CORRELATION_ID_HEADER));
    assertEquals(
            existingId,
            exchange
                    .getResponse()
                    .getHeaders()
                    .getFirst(CorrelationIdFilter.CORRELATION_ID_HEADER));
  }

  @Test
  @DisplayName("Should generate a new X-Correlation-Id when request header is blank")
  void shouldGenerateCorrelationIdWhenBlank() {
    // Arrange
    MockServerHttpRequest request =
            MockServerHttpRequest.get("/api/v1/transactions")
                    .header(CorrelationIdFilter.CORRELATION_ID_HEADER, "   ")
                    .build();
    MockServerWebExchange exchange = MockServerWebExchange.from(request);
    ArgumentCaptor<ServerWebExchange> captor = ArgumentCaptor.forClass(ServerWebExchange.class);

    // Act & Assert
    StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

    verify(chain).filter(captor.capture());
    String generatedId =
            captor
                    .getValue()
                    .getRequest()
                    .getHeaders()
                    .getFirst(CorrelationIdFilter.CORRELATION_ID_HEADER);
    assertNotNull(generatedId);
    assertNotEquals("   ", generatedId);
    assertEquals(
            generatedId,
            exchange
                    .getResponse()
                    .getHeaders()
                    .getFirst(CorrelationIdFilter.CORRELATION_ID_HEADER));
  }
}