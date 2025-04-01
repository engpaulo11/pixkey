package br.com.paulomoreira.pixkey.domain.exceptions;

public record ErrorResponse(
        int status,
        String error,
        String message
) {}