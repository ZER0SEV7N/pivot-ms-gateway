package com.pe.pivot.gateway;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Smoke test for MsGatewayApplication class definition.
 */
class MsGatewayApplicationTests {

  @Test
  @DisplayName("Should instantiate MsGatewayApplication successfully")
  void shouldInstantiateApplicationSuccessfully() {
    // Arrange & Act
    MsGatewayApplication application = new MsGatewayApplication();

    // Assert
    assertNotNull(application);
  }
}
