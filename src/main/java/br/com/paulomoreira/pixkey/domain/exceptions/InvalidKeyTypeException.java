package br.com.paulomoreira.pixkey.domain.exceptions;

public class InvalidKeyTypeException extends BusinessException {
    public InvalidKeyTypeException(String type) {
        super("Invalid key keyType: " + type + ". Must be one of: CPF, CELULAR, EMAIL, CNPJ, ALEATORIO");
    }
}
