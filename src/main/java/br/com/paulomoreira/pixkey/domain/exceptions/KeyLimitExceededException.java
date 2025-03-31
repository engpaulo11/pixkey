package br.com.paulomoreira.pixkey.domain.exceptions;

public class KeyLimitExceededException extends BusinessException {
    public KeyLimitExceededException(int maxKeys) {
        super("Maximum number of keys reached for this account: " + maxKeys);
    }
}