package com.pe.pivot.gateway.controller.dto;

import java.time.Instant;

/**
 * Response body returned by the gateway fallback controller.
 *
 * @param timestamp moment when the fallback was triggered
 * @param status HTTP status code of the fallback response
 * @param error short error description
 * @param message detailed error message
 * @param path original request path that triggered the fallback
 */
public record FallbackResponse(
    Instant timestamp,
    int status,
    String error,
    String message,
    String path) {
}
