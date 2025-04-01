package br.com.paulomoreira.pixkey.domain.validation;

import br.com.paulomoreira.pixkey.domain.exceptions.InvalidKeyException;
import br.com.paulomoreira.pixkey.domain.model.KeyType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class CelularValidatorTest {

    private final CelularValidator validator = new CelularValidator();

    @Test
    void getType_shouldReturnCelularType() {
        assertEquals(KeyType.CELULAR, validator.getType());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "+5511987654321",
            "+5511123456789"
    })
    void validate_shouldAcceptValidCelularNumbers(String validNumber) {
        assertDoesNotThrow(() -> validator.validate(validNumber));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "5511987654321",
            "+551198765432",
            "+55119876543210",
            "+5411987654321",
            "+55A1198765432",
            "+55 11987654321",
            "",
            "   "
    })
    void validate_shouldRejectInvalidCelularNumbers(String invalidNumber) {
        assertThrows(InvalidKeyException.class, () -> validator.validate(invalidNumber));
    }

    @Test
    void validate_shouldCheckCountryCode() {
        Exception exception = assertThrows(InvalidKeyException.class,
                () -> validator.validate("+5411987654321"));

        assertTrue(exception.getMessage().contains("country code '+55'"));
    }

    @Test
    void validate_shouldCheckNumberLength() {
        Exception exception = assertThrows(InvalidKeyException.class,
                () -> validator.validate("+551198765432"));

        assertTrue(exception.getMessage().contains("Celular key must have exactly 9 digits for the number"));
    }

    @Test
    void validate_shouldCheckStartsWithPlus() {
        Exception exception = assertThrows(InvalidKeyException.class,
                () -> validator.validate("5511987654321"));

        assertTrue(exception.getMessage().contains("start with '+'"));
    }

    @Test
    void validate_shouldCheckNumericPart() {
        Exception exception = assertThrows(InvalidKeyException.class,
                () -> validator.validate("+55A1198765432"));

        assertTrue(exception.getMessage().contains("only numbers"));
    }

    @Test
    void validate_shouldCheckDDDLength() {
        Exception exception = assertThrows(InvalidKeyException.class,
                () -> validator.validate("+55511987654321"));

        assertTrue(exception.getMessage().contains("Celular key must have 13-14 digits"));
    }

    @Test
    void validate_shouldCheckNumberPartLength() {
        Exception exception = assertThrows(InvalidKeyException.class,
                () -> validator.validate("+551198765432"));

        assertTrue(exception.getMessage().contains("exactly 9 digits"));
    }
}