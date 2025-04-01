package br.com.paulomoreira.pixkey.application.ports.in;

import br.com.paulomoreira.pixkey.domain.model.AccountType;
import br.com.paulomoreira.pixkey.domain.model.KeyType;
import br.com.paulomoreira.pixkey.domain.model.PixKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeactivatePixKeyUseCaseTest {

    @Mock
    private DeactivatePixKeyUseCase deactivatePixKeyUseCase;

    @Test
    void shouldDeactivatePixKeySuccessfully() {
        // Arrange
        UUID pixKeyId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        PixKey expectedPixKey = new PixKey(
                pixKeyId, KeyType.CPF, "12345678901", AccountType.CORRENTE,
                1234, 98765432, "João", "Silva",
                now.minusDays(1), false, now, true);

        when(deactivatePixKeyUseCase.execute(pixKeyId)).thenReturn(expectedPixKey);

        PixKey result = deactivatePixKeyUseCase.execute(pixKeyId);

        assertNotNull(result);
        assertFalse(result.active());
        assertNotNull(result.deactivatedAt());
        verify(deactivatePixKeyUseCase, times(1)).execute(pixKeyId);
    }
}