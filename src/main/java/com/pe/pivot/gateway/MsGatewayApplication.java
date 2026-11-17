package com.pe.pivot.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Main entry point for the Reactive API Gateway (pivot-ms-gateway :8080).
 */
@EnableDiscoveryClient
@SpringBootApplication
public class MsGatewayApplication {

  /**
   * Starts the reactive API gateway.
   *
   * @param args command line arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(MsGatewayApplication.class, args);
  }
}
