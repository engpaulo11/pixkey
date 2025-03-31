package br.com.paulomoreira.pixkey.application.ports.in;

import br.com.paulomoreira.pixkey.domain.model.AccountType;
import br.com.paulomoreira.pixkey.domain.model.KeyType;
import br.com.paulomoreira.pixkey.domain.model.PixKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetPixKeyUseCaseTest {

    @Mock
    private GetPixKeyUseCase getPixKeyUseCase;

    @Test
    void shouldReturnPixKeyWhenFound() {
        UUID pixKeyId = UUID.randomUUID();
        PixKey expectedPixKey = new PixKey(
                pixKeyId, KeyType.CPF, "12345678901", AccountType.CORRENTE,
                1234, 98765432, "João", "Silva", LocalDateTime.now(), true, null);

        when(getPixKeyUseCase.execute(pixKeyId)).thenReturn(Optional.of(expectedPixKey));

        Optional<PixKey> result = getPixKeyUseCase.execute(pixKeyId);

        assertTrue(result.isPresent());
        assertEquals(pixKeyId, result.get().id());
        verify(getPixKeyUseCase, times(1)).execute(pixKeyId);
    }

    @Test
    void shouldReturnEmptyWhenPixKeyNotFound() {
        UUID pixKeyId = UUID.randomUUID();
        when(getPixKeyUseCase.execute(pixKeyId)).thenReturn(Optional.empty());

        Optional<PixKey> result = getPixKeyUseCase.execute(pixKeyId);

        assertTrue(result.isEmpty());
        verify(getPixKeyUseCase, times(1)).execute(pixKeyId);
    }
}