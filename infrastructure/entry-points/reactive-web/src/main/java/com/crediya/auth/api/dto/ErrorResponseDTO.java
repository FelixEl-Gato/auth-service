package com.crediya.auth.api.dto;

public record ErrorResponseDTO (
        String timestamp,
        int status,
        String error,
        String message
) {
}