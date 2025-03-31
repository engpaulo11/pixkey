package br.com.paulomoreira.pixkey.adapters.inbound.dto.request;

import br.com.paulomoreira.pixkey.domain.model.AccountType;
import br.com.paulomoreira.pixkey.domain.model.KeyType;
import br.com.paulomoreira.pixkey.domain.model.PixKey;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class CreatePixKeyRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Deve converter corretamente para domínio")
    void shouldConvertToDomainCorrectly() {
        CreatePixKeyRequest request = new CreatePixKeyRequest(
                KeyType.CPF,
                "12345678901",
                AccountType.CORRENTE,
                1234,
                12345678,
                "João",
                "Silva"
        );

        PixKey pixKey = request.toDomain();

        assertAll(
                () -> assertEquals(KeyType.CPF, pixKey.type()),
                () -> assertEquals("12345678901", pixKey.keyValue()),
                () -> assertEquals(AccountType.CORRENTE, pixKey.accountType()),
                () -> assertEquals(1234, pixKey.branchNumber()),
                () -> assertEquals(12345678, pixKey.accountNumber()),
                () -> assertEquals("João", pixKey.accountHolderName()),
                () -> assertEquals("Silva", pixKey.accountHolderLastName()),
                () -> assertTrue(pixKey.active()),
                () -> assertNull(pixKey.deactivatedAt())
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    @DisplayName("Deve invalidar quando nomeCorrentista for vazio ou nulo")
    void shouldInvalidateWhenAccountHolderNameIsBlank(String invalidName) {
        CreatePixKeyRequest request = new CreatePixKeyRequest(
                KeyType.CPF,
                "12345678901",
                AccountType.CORRENTE,
                1234,
                12345678,
                invalidName,
                "Silva"
        );

        var violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("nomeCorrentista", violations.iterator().next().getPropertyPath().toString());
    }


    @ParameterizedTest
    @ValueSource(ints = {999, 10000})
    @DisplayName("Deve invalidar número de agência fora do intervalo")
    void shouldInvalidateBranchNumberOutOfRange(int invalidBranch) {
        CreatePixKeyRequest request = new CreatePixKeyRequest(
                KeyType.CPF,
                "12345678901",
                AccountType.CORRENTE,
                invalidBranch,
                12345678,
                "João",
                "Silva"
        );

        var violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals("numeroAgencia", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    @DisplayName("Deve aceitar sobrenome nulo")
    void shouldAcceptNullLastName() {
        CreatePixKeyRequest request = new CreatePixKeyRequest(
                KeyType.CPF,
                "12345678901",
                AccountType.CORRENTE,
                1234,
                12345678,
                "João",
                null
        );

        var violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve invalidar quando tipoConta for nulo")
    void shouldInvalidateWhenAccountTypeIsNull() {
        CreatePixKeyRequest request = new CreatePixKeyRequest(
                KeyType.CPF,
                "12345678901",
                null,
                1234,
                12345678,
                "João",
                "Silva"
        );

        var violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals("tipoConta", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    @DisplayName("Deve invalidar quando valorChave exceder tamanho máximo")
    void shouldInvalidateWhenKeyValueExceedsMaxLength() {
        String invalidKeyValue = "a".repeat(78); // 78 caracteres (limite é 77)

        CreatePixKeyRequest request = new CreatePixKeyRequest(
                KeyType.EMAIL,
                invalidKeyValue,
                AccountType.CORRENTE,
                1234,
                12345678,
                "João",
                "Silva"
        );

        var violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals("valorChave", violations.iterator().next().getPropertyPath().toString());
    }

    private static Stream<Arguments> provideInvalidKeyTypesAndValues() {
        return Stream.of(
                arguments(KeyType.CELULAR, "11987654321", "não corresponde ao formato esperado"),
                arguments(KeyType.CELULAR, "+55 11 987654321", "não corresponde ao formato esperado"),
                arguments(KeyType.EMAIL, "email-invalido", "não é um endereço de e-mail válido"),
                arguments(KeyType.CPF, "1234567890", "não tem 11 dígitos"),
                arguments(KeyType.CPF, "123.456.789-01", "deve conter apenas números")
        );
    }

    @Test
    @DisplayName("Deve validar objeto com todos campos corretos")
    void shouldValidateWithAllCorrectFields() {
        CreatePixKeyRequest request = new CreatePixKeyRequest(
                KeyType.CPF,
                "12345678901",
                AccountType.CORRENTE,
                1234,
                12345678,
                "João",
                "Silva"
        );

        var violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve manter valores corretos nos campos")
    void shouldKeepCorrectValues() {
        CreatePixKeyRequest request = new CreatePixKeyRequest(
                KeyType.EMAIL,
                "test@example.com",
                AccountType.POUPANCA,
                4321,
                87654321,
                "Maria",
                "Santos"
        );

        assertAll(
                () -> assertEquals(KeyType.EMAIL, request.tipoChave()),
                () -> assertEquals("test@example.com", request.valorChave()),
                () -> assertEquals(AccountType.POUPANCA, request.tipoConta()),
                () -> assertEquals(4321, request.numeroAgencia()),
                () -> assertEquals(87654321, request.numeroConta()),
                () -> assertEquals("Maria", request.nomeCorrentista()),
                () -> assertEquals("Santos", request.sobrenomeCorrentista())
        );
    }
}