package br.com.paulomoreira.pixkey.domain.validation;

import br.com.paulomoreira.pixkey.domain.exceptions.InvalidKeyException;
import br.com.paulomoreira.pixkey.domain.model.KeyType;

public class EmailValidator implements KeyValidator {
    @Override
    public KeyType getType() {
        return KeyType.EMAIL;
    }

    @Override
    public void validate(String keyValue) {
        if (keyValue == null || keyValue.isBlank()) {
            throw new InvalidKeyException("Email key cannot be null or empty");
        }
        if (!keyValue.contains("@") || keyValue.length() > 77) {
            throw new InvalidKeyException("Email key must contain '@' and not exceed 77 characters");
        }
        if (!keyValue.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new InvalidKeyException("Email key must follow a valid email format");
        }
    }
}