package br.com.paulomoreira.pixkey.domain.validation;

import br.com.paulomoreira.pixkey.domain.model.KeyType;

public interface KeyValidator {
    KeyType getType();
    void validate(String keyValue);
}