package br.com.paulomoreira.pixkey.domain.validation;

import br.com.paulomoreira.pixkey.domain.exceptions.InvalidKeyException;
import br.com.paulomoreira.pixkey.domain.model.KeyType;

public class AleatorioValidator implements KeyValidator {
    @Override
    public KeyType getType() {
        return KeyType.ALEATORIO;
    }

    @Override
    public void validate(String keyValue) {
        if (keyValue == null || keyValue.length() != 36 || !keyValue.matches("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}")) {
            throw new InvalidKeyException("Aleatorio key must be a valid UUID: " + keyValue);
        }
    }
}