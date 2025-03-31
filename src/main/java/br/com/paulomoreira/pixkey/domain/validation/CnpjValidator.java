package br.com.paulomoreira.pixkey.domain.validation;

import br.com.paulomoreira.pixkey.domain.exceptions.InvalidKeyException;
import br.com.paulomoreira.pixkey.domain.model.KeyType;

public class CnpjValidator implements KeyValidator {
    @Override
    public KeyType getType() {
        return KeyType.CNPJ;
    }

    @Override
    public void validate(String keyValue) {
        if (keyValue == null || keyValue.length() != 14 || !keyValue.matches("\\d+")) {
            throw new InvalidKeyException("CNPJ must be 14 digits: " + keyValue);
        }
        int[] digits = keyValue.chars().map(Character::getNumericValue).toArray();
        if (!validateCnpj(digits)) {
            throw new InvalidKeyException("Invalid CNPJ: " + keyValue);
        }
    }

    private boolean validateCnpj(int[] digits) {
        int[] weights1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] weights2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int sum1 = 0, sum2 = 0;
        for (int i = 0; i < 12; i++) {
            sum1 += digits[i] * weights1[i];
        }
        int digit1 = (sum1 % 11 < 2) ? 0 : 11 - (sum1 % 11);
        for (int i = 0; i < 13; i++) {
            sum2 += (i < 12 ? digits[i] : digit1) * weights2[i];
        }
        int digit2 = (sum2 % 11 < 2) ? 0 : 11 - (sum2 % 11);
        return digits[12] == digit1 && digits[13] == digit2;
    }
}