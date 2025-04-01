package br.com.paulomoreira.pixkey.domain.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidKeyTypeExceptionTest {

    @Test
    void testConstructor_ShouldSetCorrectMessage_WithValidType() {
        
        String type = "INVALID_TYPE";
        String expectedMessage = "Invalid key keyType: " + type + ". Must be one of: CPF, CELULAR, EMAIL, CNPJ, ALEATORIO";

        // Act
        InvalidKeyTypeException exception = new InvalidKeyTypeException(type);

        
        assertNotNull(exception, "A exceção não deve ser nula");
        assertEquals(expectedMessage, exception.getMessage(), "A mensagem da exceção deve corresponder ao esperado");
    }

    @Test
    void testConstructor_ShouldSetCorrectMessage_WithNullType() {
        
        String type = null;
        String expectedMessage = "Invalid key keyType: null. Must be one of: CPF, CELULAR, EMAIL, CNPJ, ALEATORIO";

        // Act
        InvalidKeyTypeException exception = new InvalidKeyTypeException(type);

        
        assertNotNull(exception, "A exceção não deve ser nula");
        assertEquals(expectedMessage, exception.getMessage(), "A mensagem da exceção deve lidar corretamente com type null");
    }

    @Test
    void testConstructor_ShouldSetCorrectMessage_WithEmptyType() {
        
        String type = "";
        String expectedMessage = "Invalid key keyType: . Must be one of: CPF, CELULAR, EMAIL, CNPJ, ALEATORIO";

        InvalidKeyTypeException exception = new InvalidKeyTypeException(type);

        assertNotNull(exception, "A exceção não deve ser nula");
        assertEquals(expectedMessage, exception.getMessage(), "A mensagem da exceção deve corresponder ao esperado com type vazio");
    }

    @Test
    void testInheritance_ShouldBeInstanceOfBusinessException() {
        InvalidKeyTypeException exception = new InvalidKeyTypeException("TEST");


        assertTrue(exception instanceof BusinessException, "A exceção deve ser uma instância de BusinessException");
    }
}