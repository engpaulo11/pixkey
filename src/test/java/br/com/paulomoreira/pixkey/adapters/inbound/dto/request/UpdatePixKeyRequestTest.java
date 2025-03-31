package br.com.paulomoreira.pixkey.adapters.inbound.dto.request;

import br.com.paulomoreira.pixkey.domain.model.AccountType;
import br.com.paulomoreira.pixkey.domain.model.KeyType;
import br.com.paulomoreira.pixkey.domain.model.PixKey;
import jakarta.validation.ConstraintViolation;
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

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UpdatePixKeyRequestTest {

    private static Validator validator;
    private static final PixKey EXISTING_KEY = new PixKey(
            UUID.randomUUID(),
            KeyType.CPF,
            "12345678901",
            AccountType.CORRENTE,
            1234,
            12345678,
            "João",
            "Silva",
            LocalDateTime.now(),
            true,
            null
    );

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Deve criar objeto válido com todos campos corretos")
    void shouldCreateValidObject() {
        UpdatePixKeyRequest request = new UpdatePixKeyRequest(
                AccountType.POUPANCA,
                4321,
                87654321,
                "Maria",
                "Santos"
        );

        Set<ConstraintViolation<UpdatePixKeyRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    @DisplayName("Deve invalidar quando nomeCorrentista for vazio ou nulo")
    void shouldInvalidateBlankAccountHolderName(String invalidName) {
        UpdatePixKeyRequest request = new UpdatePixKeyRequest(
                AccountType.CORRENTE,
                1234,
                12345678,
                invalidName,
                "Silva"
        );

        Set<ConstraintViolation<UpdatePixKeyRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("nomeCorrentista", violations.iterator().next().getPropertyPath().toString());
    }

    @ParameterizedTest
    @ValueSource(ints = {999, 10000})
    @DisplayName("Deve invalidar número de agência fora do intervalo 1000-9999")
    void shouldInvalidateBranchNumberOutOfRange(int invalidBranch) {
        UpdatePixKeyRequest request = new UpdatePixKeyRequest(
                AccountType.CORRENTE,
                invalidBranch,
                12345678,
                "João",
                "Silva"
        );

        Set<ConstraintViolation<UpdatePixKeyRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("numeroAgencia", violations.iterator().next().getPropertyPath().toString());
    }

    @ParameterizedTest
    @ValueSource(ints = {9999999, 100000000})
    @DisplayName("Deve invalidar número de conta fora do intervalo 10000000-99999999")
    void shouldInvalidateAccountNumberOutOfRange(int invalidAccount) {
        UpdatePixKeyRequest request = new UpdatePixKeyRequest(
                AccountType.CORRENTE,
                1234,
                invalidAccount,
                "João",
                "Silva"
        );

        Set<ConstraintViolation<UpdatePixKeyRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("numeroConta", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    @DisplayName("Deve aceitar sobrenome nulo")
    void shouldAcceptNullLastName() {
        UpdatePixKeyRequest request = new UpdatePixKeyRequest(
                AccountType.CORRENTE,
                1234,
                12345678,
                "João",
                null
        );

        Set<ConstraintViolation<UpdatePixKeyRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Deve invalidar quando tipoConta for nulo")
    void shouldInvalidateNullAccountType() {
        UpdatePixKeyRequest request = new UpdatePixKeyRequest(
                null,
                1234,
                12345678,
                "João",
                "Silva"
        );

        Set<ConstraintViolation<UpdatePixKeyRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("tipoConta", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    @DisplayName("Deve converter corretamente para domínio mantendo campos imutáveis")
    void shouldConvertToDomainKeepingImmutableFields() {
        UpdatePixKeyRequest request = new UpdatePixKeyRequest(
                AccountType.POUPANCA,
                4321,
                87654321,
                "Maria",
                "Santos"
        );

        PixKey updatedKey = request.toDomain(EXISTING_KEY);

        assertAll("Verificação dos campos imutáveis",
                () -> assertEquals(EXISTING_KEY.id(), updatedKey.id()),
                () -> assertEquals(EXISTING_KEY.type(), updatedKey.type()),
                () -> assertEquals(EXISTING_KEY.keyValue(), updatedKey.keyValue()),
                () -> assertEquals(EXISTING_KEY.createdAt(), updatedKey.createdAt()),
                () -> assertEquals(EXISTING_KEY.active(), updatedKey.active()),
                () -> assertEquals(EXISTING_KEY.deactivatedAt(), updatedKey.deactivatedAt())
        );

        assertAll("Verificação dos campos atualizáveis",
                () -> assertEquals(AccountType.POUPANCA, updatedKey.accountType()),
                () -> assertEquals(4321, updatedKey.branchNumber()),
                () -> assertEquals(87654321, updatedKey.accountNumber()),
                () -> assertEquals("Maria", updatedKey.accountHolderName()),
                () -> assertEquals("Santos", updatedKey.accountHolderLastName())
        );
    }

    @ParameterizedTest
    @MethodSource("provideLongNames")
    @DisplayName("Deve invalidar quando nome ou sobrenome exceder tamanho máximo")
    void shouldInvalidateWhenNameExceedsMaxLength(String name, String lastName, String expectedField) {
        UpdatePixKeyRequest request = new UpdatePixKeyRequest(
                AccountType.CORRENTE,
                1234,
                12345678,
                name,
                lastName
        );

        Set<ConstraintViolation<UpdatePixKeyRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals(expectedField, violations.iterator().next().getPropertyPath().toString());
    }

    private static Stream<Arguments> provideLongNames() {
        return Stream.of(
                Arguments.of("N".repeat(31), "Silva", "nomeCorrentista"),
                Arguments.of("João", "S".repeat(46), "sobrenomeCorrentista")
        );
    }

    @Test
    @DisplayName("Deve manter campos originais quando valores são iguais")
    void shouldKeepOriginalValuesWhenNoChanges() {
        UpdatePixKeyRequest request = new UpdatePixKeyRequest(
                EXISTING_KEY.accountType(),
                EXISTING_KEY.branchNumber(),
                EXISTING_KEY.accountNumber(),
                EXISTING_KEY.accountHolderName(),
                EXISTING_KEY.accountHolderLastName()
        );

        PixKey updatedKey = request.toDomain(EXISTING_KEY);
        assertEquals(EXISTING_KEY, updatedKey);
    }

    @Test
    @DisplayName("Deve atualizar apenas campos modificados")
    void shouldUpdateOnlyChangedFields() {
        UpdatePixKeyRequest request = new UpdatePixKeyRequest(
                EXISTING_KEY.accountType(),
                EXISTING_KEY.branchNumber(),
                EXISTING_KEY.accountNumber(),
                "Novo Nome",
                EXISTING_KEY.accountHolderLastName()
        );

        PixKey updatedKey = request.toDomain(EXISTING_KEY);
        assertEquals("Novo Nome", updatedKey.accountHolderName());
        assertEquals(EXISTING_KEY.accountHolderLastName(), updatedKey.accountHolderLastName());
    }

    @Test
    @DisplayName("Deve permitir atualização para conta poupança")
    void shouldAllowUpdateToPoupancaAccount() {
        UpdatePixKeyRequest request = new UpdatePixKeyRequest(
                AccountType.POUPANCA,
                EXISTING_KEY.branchNumber(),
                EXISTING_KEY.accountNumber(),
                EXISTING_KEY.accountHolderName(),
                EXISTING_KEY.accountHolderLastName()
        );

        PixKey updatedKey = request.toDomain(EXISTING_KEY);
        assertEquals(AccountType.POUPANCA, updatedKey.accountType());
    }
}