package br.com.paulomoreira.pixkey.domain.validation;

import br.com.paulomoreira.pixkey.domain.exceptions.InvalidKeyException;
import br.com.paulomoreira.pixkey.domain.model.KeyType;

public class CelularValidator implements KeyValidator {
    @Override
    public void validate(String keyValue) {
        // Verificar se começa com "+"
        if (!keyValue.startsWith("+")) {
            throw new InvalidKeyException("Celular key must start with '+'");
        }

        // Extrair a parte numérica
        String numericPart = keyValue.substring(1); // Remove o "+"
        if (!numericPart.matches("\\d+")) {
            throw new InvalidKeyException("Celular key must contain only numbers after '+'");
        }

        // Verificar comprimento total (mínimo 13, máximo 14 para Brasil)
        if (numericPart.length() < 12 || numericPart.length() > 13) {
            throw new InvalidKeyException("Celular key must have 13-14 digits (country code + DDD + number)");
        }

        // Extrair código do país (assumindo 2 dígitos para Brasil: +55)
        String countryCode = numericPart.substring(0, 2);
        if (!countryCode.equals("55")) { // Validação específica para Brasil
            throw new InvalidKeyException("Celular key must have country code '+55' for Brazil");
        }

        // Extrair DDD (2 dígitos)
        String ddd = numericPart.substring(2, 4);
        if (ddd.length() != 2) {
            throw new InvalidKeyException("Celular key must have a 2-digit DDD");
        }

        // Extrair número (9 dígitos)
        String number = numericPart.substring(4);
        if (number.length() != 9) {
            throw new InvalidKeyException("Celular key must have exactly 9 digits for the number");
        }
    }

    @Override
    public KeyType getType() {
        return KeyType.CELULAR;
    }
}