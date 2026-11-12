package com.pe.pivot.gateway.controller.dto;

import java.time.Instant;

public record FallbackResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path
) {
}