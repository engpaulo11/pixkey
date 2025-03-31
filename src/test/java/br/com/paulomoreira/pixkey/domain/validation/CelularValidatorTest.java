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
            "+5511987654321",   // Formato válido sem espaços
            "+5511123456789"    // Número válido com 13 dígitos totais
    })
    void validate_shouldAcceptValidCelularNumbers(String validNumber) {
        assertDoesNotThrow(() -> validator.validate(validNumber));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "5511987654321",    // Falta o "+"
            "+551198765432",    // Muito curto (11 dígitos)
            "+55119876543210",  // Muito longo (14 dígitos)
            "+5411987654321",   // Código de país errado (não é Brasil)
            "+55A1198765432",  // Caracteres não numéricos
            "+55 11987654321",  // Contém espaço (deveria ser tratado antes)
            "",                // Vazio
            "   "             // Espaços em branco
    })
    void validate_shouldRejectInvalidCelularNumbers(String invalidNumber) {
        assertThrows(InvalidKeyException.class, () -> validator.validate(invalidNumber));
    }

    @Test
    void validate_shouldCheckCountryCode() {
        Exception exception = assertThrows(InvalidKeyException.class,
                () -> validator.validate("+5411987654321")); // Argentina

        assertTrue(exception.getMessage().contains("country code '+55'"));
    }

    @Test
    void validate_shouldCheckNumberLength() {
        Exception exception = assertThrows(InvalidKeyException.class,
                () -> validator.validate("+551198765432")); // 11 dígitos

        assertTrue(exception.getMessage().contains("Celular key must have exactly 9 digits for the number"));
    }

    @Test
    void validate_shouldCheckStartsWithPlus() {
        Exception exception = assertThrows(InvalidKeyException.class,
                () -> validator.validate("5511987654321")); // Sem "+"

        assertTrue(exception.getMessage().contains("start with '+'"));
    }

    @Test
    void validate_shouldCheckNumericPart() {
        Exception exception = assertThrows(InvalidKeyException.class,
                () -> validator.validate("+55A1198765432")); // Letra no número

        assertTrue(exception.getMessage().contains("only numbers"));
    }

    @Test
    void validate_shouldCheckDDDLength() {
        Exception exception = assertThrows(InvalidKeyException.class,
                () -> validator.validate("+55511987654321")); // DDD com 1 dígito

        assertTrue(exception.getMessage().contains("Celular key must have 13-14 digits"));
    }

    @Test
    void validate_shouldCheckNumberPartLength() {
        Exception exception = assertThrows(InvalidKeyException.class,
                () -> validator.validate("+551198765432")); // Número com 8 dígitos

        assertTrue(exception.getMessage().contains("exactly 9 digits"));
    }
}