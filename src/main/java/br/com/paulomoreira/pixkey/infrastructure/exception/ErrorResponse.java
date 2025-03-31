package br.com.paulomoreira.pixkey.infrastructure.exception;

public record ErrorResponse(
        int status,
        String error,
        String message
) {}