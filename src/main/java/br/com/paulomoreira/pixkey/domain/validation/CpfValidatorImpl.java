package br.com.paulomoreira.pixkey.domain.validation;

import br.com.paulomoreira.pixkey.domain.exceptions.InvalidKeyException;
import br.com.paulomoreira.pixkey.domain.model.KeyType;
import br.com.paulomoreira.pixkey.domain.utils.CpfValidator;

public class CpfValidatorImpl implements KeyValidator {
    @Override
    public KeyType getType() {
        return KeyType.CPF;
    }

    @Override
    public void validate(String keyValue) {
        if (!CpfValidator.isValid(keyValue)) {
            throw new InvalidKeyException("Invalid CPF: " + keyValue);
        }
    }
}