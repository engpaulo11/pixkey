package br.com.paulomoreira.pixkey.domain.exceptions;

import java.util.UUID;

public class KeyNotFoundException extends BusinessException {
    public KeyNotFoundException(UUID id) {
        super("Key not found: " + id);
    }
}