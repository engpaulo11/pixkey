// InvalidKeyException.java
package br.com.paulomoreira.pixkey.domain.exceptions;

public class InvalidKeyException extends BusinessException {
    public InvalidKeyException(String message) {
        super(message);
    }
}