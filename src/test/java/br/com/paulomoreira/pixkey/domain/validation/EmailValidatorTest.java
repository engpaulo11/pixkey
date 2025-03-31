package br.com.paulomoreira.pixkey.domain.validation;

import br.com.paulomoreira.pixkey.domain.exceptions.InvalidKeyException;
import br.com.paulomoreira.pixkey.domain.model.KeyType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class EmailValidatorTest {

    private final EmailValidator validator = new EmailValidator();

    @Test
    void getType_shouldReturnEmailType() {
        assertEquals(KeyType.EMAIL, validator.getType());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "user@example.com",
            "firstname.lastname@example.com",
            "email@subdomain.example.com",
            "1234567890@example.com",
            "email@example-one.com",
            "_______@example.com",
            "email@example.name",
            "email@example.museum",
            "email@example.co.jp",
            "firstname-lastname@example.com"
    })
    void validate_shouldAcceptValidEmails(String validEmail) {
        assertDoesNotThrow(() -> validator.validate(validEmail));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",                      // Vazio
            "   ",                   // Espaços em branco,                    // Nulo
            "plainaddress",          // Falta @
            "@missingusername.com",  // Falta parte local
            "username@.com",        // Falta domínio
            "username@example_com",  // Underscore no domínio
            "a@b.c",                 // Domínio muito curto
            "user name@example.com", // Espaço no nome
            "user\name@example.com", // Caractere inválido
            "user@example@example.com", // Múltiplos @
    })
    void validate_shouldRejectInvalidEmails(String invalidEmail) {
        assertThrows(InvalidKeyException.class, () -> validator.validate(invalidEmail));
    }

    @Test
    void validate_shouldCheckNullEmpty() {
        Exception nullException = assertThrows(InvalidKeyException.class,
                () -> validator.validate(null));
        assertTrue(nullException.getMessage().contains("null or empty"));

        Exception emptyException = assertThrows(InvalidKeyException.class,
                () -> validator.validate(""));
        assertTrue(emptyException.getMessage().contains("null or empty"));
    }

    @Test
    void validate_shouldCheckAtSymbol() {
        Exception exception = assertThrows(InvalidKeyException.class,
                () -> validator.validate("noatsymbol.com"));

        assertTrue(exception.getMessage().contains("must contain '@'"));
    }


    @Test
    void validate_shouldCheckEmailFormat() {
        Exception exception = assertThrows(InvalidKeyException.class,
                () -> validator.validate("invalid@email"));

        assertTrue(exception.getMessage().contains("valid email format"));
    }
}