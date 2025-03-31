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
        // Configurar um PixKey válido para os testes
        validPixKey = new PixKey(
                null, // ID será gerado automaticamente
                KeyType.CELULAR,
                "+5521994827834",
                AccountType.CORRENTE,
                1234,
                56789012,
                "Paulo",
                "Moreira",
                null, // createdAt será gerado automaticamente
                true,
                null
        );

        // Inicializar o caso de uso sem stubbing no setUp
        createPixKeyUseCase = new CreatePixKeyUseCaseImpl(repository, List.of(celularValidator));
    }

    @Test
    void shouldThrowInvalidKeyTypeExceptionWhenKeyTypeIsInvalid() {
        // Arrange
        assertThrows(NullPointerException.class, () -> new PixKey(
                null,
                null, // Tipo inválido (null)
                "+5521994827834",
                AccountType.CORRENTE,
                1234,
                56789012,
                "Paulo",
                "Moreira",
                null,
                true,
                null
        ));

        // Não precisa de stubbing aqui, pois o teste falha no construtor do PixKey
    }

    @Test
    void shouldThrowInvalidKeyExceptionWhenNoValidatorFound() {
        // Arrange
        PixKey pixKeyWithoutValidator = new PixKey(
                null,
                KeyType.EMAIL, // Tipo válido, mas sem validador correspondente
                "usuario@dominio.com",
                AccountType.CORRENTE,
                1234,
                56789012,
                "Paulo",
                "Moreira",
                null,
                true,
                null
        );

        // Act & Assert
        InvalidKeyException exception = assertThrows(InvalidKeyException.class, () -> {
            createPixKeyUseCase.execute(pixKeyWithoutValidator);
        });
        assertEquals("No validator found for key type: EMAIL", exception.getMessage());
    }

    @Test
    void shouldThrowDuplicateKeyExceptionWhenKeyValueAlreadyExists() {
        // Arrange
        when(celularValidator.getType()).thenReturn(KeyType.CELULAR); // Stubbing necessário aqui
        when(repository.existsByKeyValue(validPixKey.keyValue())).thenReturn(true);

        // Act & Assert
        DuplicateKeyException exception = assertThrows(DuplicateKeyException.class, () -> {
            createPixKeyUseCase.execute(validPixKey);
        });
        assertEquals("Key already exists: +5521994827834", exception.getMessage());
        verify(celularValidator).validate(validPixKey.keyValue());
    }

    @Test
    void shouldThrowKeyLimitExceededExceptionWhenLimitIsExceededForPessoaFisica() {
        // Arrange
        when(celularValidator.getType()).thenReturn(KeyType.CELULAR); // Stubbing necessário aqui
        when(repository.existsByKeyValue(validPixKey.keyValue())).thenReturn(false);
        when(repository.countByAccount(1234, 56789012)).thenReturn(5); // Limite PF = 5

        // Act & Assert
        KeyLimitExceededException exception = assertThrows(KeyLimitExceededException.class, () -> {
            createPixKeyUseCase.execute(validPixKey);
        });
        assertEquals("Maximum number of keys reached for this account: 5", exception.getMessage());
        verify(celularValidator).validate(validPixKey.keyValue());
    }

    @Test
    void shouldThrowKeyLimitExceededExceptionWhenLimitIsExceededForPessoaJuridica() {
        // Arrange
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
                null
        );
        KeyValidator cnpjValidator = mock(KeyValidator.class);
        when(cnpjValidator.getType()).thenReturn(KeyType.CNPJ);
        createPixKeyUseCase = new CreatePixKeyUseCaseImpl(repository, List.of(cnpjValidator));

        when(repository.existsByKeyValue(cnpjPixKey.keyValue())).thenReturn(false);
        when(repository.countByAccount(1234, 56789012)).thenReturn(20); // Limite PJ = 20

        // Act & Assert
        KeyLimitExceededException exception = assertThrows(KeyLimitExceededException.class, () -> {
            createPixKeyUseCase.execute(cnpjPixKey);
        });
        assertEquals("Maximum number of keys reached for this account: 20", exception.getMessage());
        verify(cnpjValidator).validate(cnpjPixKey.keyValue());
    }

    @Test
    void shouldCreatePixKeySuccessfullyWhenAllConditionsAreMet() {
        // Arrange
        when(celularValidator.getType()).thenReturn(KeyType.CELULAR); // Stubbing necessário aqui
        when(repository.existsByKeyValue(validPixKey.keyValue())).thenReturn(false);
        when(repository.countByAccount(1234, 56789012)).thenReturn(2); // Abaixo do limite
        when(repository.save(any(PixKey.class))).thenReturn(validPixKey);

        // Act
        PixKey result = createPixKeyUseCase.execute(validPixKey);

        // Assert
        assertNotNull(result);
        assertNotNull(result.id()); // ID gerado
        assertNotNull(result.createdAt()); // Data gerada
        assertEquals(validPixKey.keyValue(), result.keyValue());
        assertEquals(validPixKey.type(), result.type());
        assertTrue(result.active());
        assertNull(result.deactivatedAt());
        verify(celularValidator).validate(validPixKey.keyValue());
        verify(repository).save(any(PixKey.class));
    }
}