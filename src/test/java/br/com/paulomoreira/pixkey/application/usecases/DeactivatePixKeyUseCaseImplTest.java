package br.com.paulomoreira.pixkey.application.usecases;

import br.com.paulomoreira.pixkey.application.ports.out.PixKeyRepository;
import br.com.paulomoreira.pixkey.domain.exceptions.KeyAlreadyInactiveException;
import br.com.paulomoreira.pixkey.domain.exceptions.KeyNotFoundException;
import br.com.paulomoreira.pixkey.domain.model.AccountType;
import br.com.paulomoreira.pixkey.domain.model.KeyType;
import br.com.paulomoreira.pixkey.domain.model.PixKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeactivatePixKeyUseCaseImplTest {

    @Mock
    private PixKeyRepository repository;

    @InjectMocks
    private DeactivatePixKeyUseCaseImpl useCase;

    @Test
    void shouldThrowKeyNotFoundExceptionWhenKeyDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        KeyNotFoundException exception = assertThrows(KeyNotFoundException.class, () -> useCase.execute(id));
        assertEquals("Key not found: " + id, exception.getMessage());
    }

    @Test
    void shouldThrowKeyAlreadyInactiveExceptionWhenKeyIsInactive() {
        UUID id = UUID.randomUUID();
        PixKey inactiveKey = new PixKey(id, KeyType.CELULAR, "+5521994827834", AccountType.CORRENTE,
                1234, 56789012, "Paulo",
                "Moreira", null, false, null, true);
        when(repository.findById(id)).thenReturn(Optional.of(inactiveKey));

        KeyAlreadyInactiveException exception = assertThrows(KeyAlreadyInactiveException.class, () -> useCase.execute(id));
        assertEquals("Key is already inactive: " + id, exception.getMessage());
    }
}