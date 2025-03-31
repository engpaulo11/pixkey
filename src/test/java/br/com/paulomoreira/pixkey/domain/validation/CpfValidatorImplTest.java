package br.com.paulomoreira.pixkey.domain.validation;

import br.com.paulomoreira.pixkey.domain.exceptions.InvalidKeyException;
import br.com.paulomoreira.pixkey.domain.model.KeyType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class CpfValidatorImplTest {

    private final CpfValidatorImpl validator = new CpfValidatorImpl();

    @Test
    void getType_shouldReturnCpfType() {
        assertEquals(KeyType.CPF, validator.getType());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "52998224725",     // CPF válido sem máscara
    })
    void validate_shouldAcceptValidCpfs(String validCpf) {
        assertDoesNotThrow(() -> validator.validate(validCpf));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "123.456.789-00",  // CPF inválido (dígitos verificadores errados)
            "111.111.111-11",  // CPF inválido (todos dígitos iguais)
            "123",             // Muito curto
            "123456789012345", // Muito longo
            "ABC.DEF.GHI-JK",  // Caracteres não numéricos
            "",                // Vazio
            "   ",             // Espaços em branco
    })
    void validate_shouldRejectInvalidCpfs(String invalidCpf) {
        assertThrows(InvalidKeyException.class, () -> validator.validate(invalidCpf));
    }

    @Test
    void validate_shouldIncludeKeyValueInExceptionMessage() {
        String invalidCpf = "123.456.789-00";
        Exception exception = assertThrows(InvalidKeyException.class,
                () -> validator.validate(invalidCpf));

        assertTrue(exception.getMessage().contains(invalidCpf));
    }
}