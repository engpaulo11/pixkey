package br.com.paulomoreira.pixkey.application.usecases;

import br.com.paulomoreira.pixkey.application.ports.out.PixKeyRepository;
import br.com.paulomoreira.pixkey.domain.exceptions.DuplicateKeyException;
import br.com.paulomoreira.pixkey.domain.exceptions.InvalidKeyException;
import br.com.paulomoreira.pixkey.domain.exceptions.KeyLimitExceededException;
import br.com.paulomoreira.pixkey.domain.model.AccountType;
import br.com.paulomoreira.pixkey.domain.model.KeyType;
import br.com.paulomoreira.pixkey.domain.model.PixKey;
import br.com.paulomoreira.pixkey.domain.validation.KeyValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreatePixKeyUseCaseImplTest {

    @Mock
    private PixKeyRepository repository;

    @Mock
    private KeyValidator celularValidator;

    @InjectMocks
    private CreatePixKeyUseCaseImpl createPixKeyUseCase;

    private PixKey validPixKey;

    @BeforeEach
    void setUp() {
        validPixKey = new PixKey(
                null,
                KeyType.CELULAR,
                "+5521994827834",
                AccountType.CORRENTE,
                1234,
                56789012,
                "Paulo",
                "Moreira",
                null,
                true,
                null,
                true
        );

        createPixKeyUseCase = new CreatePixKeyUseCaseImpl(repository, List.of(celularValidator));
    }

    @Test
    void shouldThrowInvalidKeyTypeExceptionWhenKeyTypeIsInvalid() {
        assertThrows(NullPointerException.class, () -> new PixKey(
                null,
                null,
                "+5521994827834",
                AccountType.CORRENTE,
                1234,
                56789012,
                "Paulo",
                "Moreira",
                null,
                true,
                null,
                true
        ));

    }

    @Test
    void shouldThrowInvalidKeyExceptionWhenNoValidatorFound() {

        PixKey pixKeyWithoutValidator = new PixKey(
                null,
                KeyType.EMAIL,
                "usuario@dominio.com",
                AccountType.CORRENTE,
                1234,
                56789012,
                "Paulo",
                "Moreira",
                null,
                true,
                null,
                true
        );

        InvalidKeyException exception = assertThrows(InvalidKeyException.class, () -> {
            createPixKeyUseCase.execute(pixKeyWithoutValidator);
        });
        assertEquals("No validator found for key keyType: EMAIL", exception.getMessage());
    }

    @Test
    void shouldThrowDuplicateKeyExceptionWhenKeyValueAlreadyExists() {

        when(celularValidator.getType()).thenReturn(KeyType.CELULAR); // Stubbing necessário aqui
        when(repository.existsByKeyValue(validPixKey.keyValue())).thenReturn(true);


        DuplicateKeyException exception = assertThrows(DuplicateKeyException.class, () -> {
            createPixKeyUseCase.execute(validPixKey);
        });
        assertEquals("Key already exists: +5521994827834", exception.getMessage());
        verify(celularValidator).validate(validPixKey.keyValue());
    }

    @Test
    void shouldThrowKeyLimitExceededExceptionWhenLimitIsExceededForPessoaFisica() {

        when(celularValidator.getType()).thenReturn(KeyType.CELULAR); // Stubbing necessário aqui
        when(repository.existsByKeyValue(validPixKey.keyValue())).thenReturn(false);
        when(repository.countByAccount(1234, 56789012)).thenReturn(5); // Limite PF = 5


        KeyLimitExceededException exception = assertThrows(KeyLimitExceededException.class, () -> {
            createPixKeyUseCase.execute(validPixKey);
        });
        assertEquals("Maximum number of keys reached for this account: 5", exception.getMessage());
        verify(celularValidator).validate(validPixKey.keyValue());
    }

    @Test
    void shouldThrowKeyLimitExceededExceptionWhenLimitIsExceededForPessoaJuridica() {

        PixKey cnpjPixKey = new PixKey(
                null,
                KeyType.CNPJ,
                "12345678000195",
                AccountType.CORRENTE,
                1234,
                56789012,
                "Empresa XYZ",
                null,
                null,
                true,
                null,
                false
        );
        KeyValidator cnpjValidator = mock(KeyValidator.class);
        when(cnpjValidator.getType()).thenReturn(KeyType.CNPJ);
        createPixKeyUseCase = new CreatePixKeyUseCaseImpl(repository, List.of(cnpjValidator));

        when(repository.existsByKeyValue(cnpjPixKey.keyValue())).thenReturn(false);
        when(repository.countByAccount(1234, 56789012)).thenReturn(20); // Limite PJ = 20


        KeyLimitExceededException exception = assertThrows(KeyLimitExceededException.class, () -> {
            createPixKeyUseCase.execute(cnpjPixKey);
        });
        assertEquals("Maximum number of keys reached for this account: 20", exception.getMessage());
        verify(cnpjValidator).validate(cnpjPixKey.keyValue());
    }

    @Test
    void shouldCreatePixKeySuccessfullyWhenAllConditionsAreMet() {

        when(celularValidator.getType()).thenReturn(KeyType.CELULAR);
        when(repository.existsByKeyValue(validPixKey.keyValue())).thenReturn(false);
        when(repository.countByAccount(1234, 56789012)).thenReturn(2);
        when(repository.save(any(PixKey.class))).thenReturn(validPixKey);

        PixKey result = createPixKeyUseCase.execute(validPixKey);

        assertNotNull(result);
        assertNotNull(result.id());
        assertNotNull(result.createdAt());
        assertEquals(validPixKey.keyValue(), result.keyValue());
        assertEquals(validPixKey.type(), result.type());
        assertTrue(result.active());
        assertNull(result.deactivatedAt());
        verify(celularValidator).validate(validPixKey.keyValue());
        verify(repository).save(any(PixKey.class));
    }
}