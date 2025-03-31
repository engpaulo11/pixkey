package br.com.paulomoreira.pixkey.domain.exceptions;

public class DuplicateKeyException extends BusinessException {
    public DuplicateKeyException(String keyValue) {
        super("Key already exists: " + keyValue);
    }
}