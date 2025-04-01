package br.com.paulomoreira.pixkey.adapters.in.dto.response;

import br.com.paulomoreira.pixkey.domain.model.AccountType;
import br.com.paulomoreira.pixkey.domain.model.KeyType;
import br.com.paulomoreira.pixkey.domain.model.PixKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PixKeyResponseTest {

    private static final UUID TEST_ID = UUID.randomUUID();
    private static final LocalDateTime TEST_CREATED_AT = LocalDateTime.now();
    private static final LocalDateTime TEST_DEACTIVATED_AT = TEST_CREATED_AT.plusDays(1);

    @Test
    @DisplayName("Deve converter corretamente PixKey ativa para Response")
    void shouldConvertActivePixKeyToResponse() {
        PixKey pixKey = new PixKey(
                TEST_ID,
                KeyType.CPF,
                "12345678901",
                AccountType.CORRENTE,
                1234,
                98765432,
                "João",
                "Silva",
                TEST_CREATED_AT,
                true,
                null
        );

        PixKeyResponse response = PixKeyResponse.fromDomain(pixKey);

        assertAll("Verificação dos campos da resposta",
                () -> assertEquals(TEST_ID.toString(), response.id()),
                () -> assertEquals("CPF", response.tipoChave()),
                () -> assertEquals("12345678901", response.valorChave()),
                () -> assertEquals("CORRENTE", response.tipoConta()),
                () -> assertEquals(1234, response.numeroAgencia()),
                () -> assertEquals(98765432, response.numeroConta()),
                () -> assertEquals("João", response.nomeCorrentista()),
                () -> assertEquals("Silva", response.sobrenomeCorrentista()),
                () -> assertEquals(TEST_CREATED_AT.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), response.dataHoraInclusao()),
                () -> assertNull(response.dataHoraInativacao())
        );
    }

    @Test
    @DisplayName("Deve converter corretamente PixKey inativa para Response")
    void shouldConvertInactivePixKeyToResponse() {
        PixKey pixKey = new PixKey(
                TEST_ID,
                KeyType.EMAIL,
                "test@example.com",
                AccountType.POUPANCA,
                4321,
                12345678,
                "Maria",
                null,
                TEST_CREATED_AT,
                false,
                TEST_DEACTIVATED_AT
        );

        PixKeyResponse response = PixKeyResponse.fromDomain(pixKey);

        assertAll("Verificação dos campos da resposta inativa",
                () -> assertEquals(TEST_ID.toString(), response.id()),
                () -> assertEquals("EMAIL", response.tipoChave()),
                () -> assertEquals("test@example.com", response.valorChave()),
                () -> assertEquals("POUPANCA", response.tipoConta()),
                () -> assertEquals(4321, response.numeroAgencia()),
                () -> assertEquals(12345678, response.numeroConta()),
                () -> assertEquals("Maria", response.nomeCorrentista()),
                () -> assertNull(response.sobrenomeCorrentista()),
                () -> assertEquals(TEST_CREATED_AT.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), response.dataHoraInclusao()),
                () -> assertEquals(TEST_DEACTIVATED_AT.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), response.dataHoraInativacao())
        );
    }

    @ParameterizedTest
    @EnumSource(KeyType.class)
    @DisplayName("Deve converter corretamente todos os tipos de chave")
    void shouldConvertAllKeyTypes(KeyType keyType) {
        PixKey pixKey = new PixKey(
                TEST_ID,
                keyType,
                getSampleKeyValue(keyType),
                AccountType.CORRENTE,
                1234,
                98765432,
                "João",
                "Silva",
                TEST_CREATED_AT,
                true,
                null
        );

        PixKeyResponse response = PixKeyResponse.fromDomain(pixKey);
        assertEquals(keyType.name(), response.tipoChave());
    }

    @ParameterizedTest
    @EnumSource(AccountType.class)
    @DisplayName("Deve converter corretamente todos os tipos de conta")
    void shouldConvertAllAccountTypes(AccountType accountType) {
        PixKey pixKey = new PixKey(
                TEST_ID,
                KeyType.CPF,
                "12345678901",
                accountType,
                1234,
                98765432,
                "João",
                "Silva",
                TEST_CREATED_AT,
                true,
                null
        );

        PixKeyResponse response = PixKeyResponse.fromDomain(pixKey);
        assertEquals(accountType.name(), response.tipoConta());
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("Deve lidar corretamente com sobrenome nulo")
    void shouldHandleNullLastName(String lastName) {
        PixKey pixKey = new PixKey(
                TEST_ID,
                KeyType.CPF,
                "12345678901",
                AccountType.CORRENTE,
                1234,
                98765432,
                "João",
                lastName,
                TEST_CREATED_AT,
                true,
                null
        );

        PixKeyResponse response = PixKeyResponse.fromDomain(pixKey);
        assertNull(response.sobrenomeCorrentista());
    }

    @Test
    @DisplayName("Deve formatar corretamente as datas")
    void shouldFormatDatesCorrectly() {
        LocalDateTime testDate = LocalDateTime.of(2023, 6, 15, 14, 30, 45);
        String expectedDate = "2023-06-15T14:30:45";

        PixKey pixKey = new PixKey(
                TEST_ID,
                KeyType.CPF,
                "12345678901",
                AccountType.CORRENTE,
                1234,
                98765432,
                "João",
                "Silva",
                testDate,
                false,
                testDate.plusDays(1)
        );

        PixKeyResponse response = PixKeyResponse.fromDomain(pixKey);

        assertAll("Verificação da formatação das datas",
                () -> assertEquals(expectedDate, response.dataHoraInclusao()),
                () -> assertEquals("2023-06-16T14:30:45", response.dataHoraInativacao())
        );
    }

    @Test
    @DisplayName("Deve lidar corretamente com data de inativação nula")
    void shouldHandleNullDeactivationDate() {
        PixKey pixKey = new PixKey(
                TEST_ID,
                KeyType.CPF,
                "12345678901",
                AccountType.CORRENTE,
                1234,
                98765432,
                "João",
                "Silva",
                TEST_CREATED_AT,
                true,
                null
        );

        PixKeyResponse response = PixKeyResponse.fromDomain(pixKey);
        assertNull(response.dataHoraInativacao());
    }

    private String getSampleKeyValue(KeyType keyType) {
        return switch (keyType) {
            case CELULAR -> "+5511987654321";
            case EMAIL -> "user@example.com";
            case CPF -> "12345678901";
            case CNPJ -> "12345678901234";
            case ALEATORIO -> "a1b2c3d4-e5f6-4a3d-8f2e-1b3a5c7d9f0e";
        };
    }
}