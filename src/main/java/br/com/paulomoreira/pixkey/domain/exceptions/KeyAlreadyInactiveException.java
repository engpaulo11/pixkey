package br.com.paulomoreira.pixkey.domain.exceptions;

import java.util.UUID;

public class KeyAlreadyInactiveException extends BusinessException {
    public KeyAlreadyInactiveException(UUID id) {
        super("Key is already inactive: " + id);
    }
}