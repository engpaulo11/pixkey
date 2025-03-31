package br.com.paulomoreira.pixkey.domain.exceptions;

public class InvalidFilterCombinationException extends BusinessException {
    public InvalidFilterCombinationException(String message) {
        super(message);
    }
}