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
class UpdatePixKeyUseCaseTest {

    @Mock
    private UpdatePixKeyUseCase updatePixKeyUseCase;

    @Test
    void shouldUpdatePixKeySuccessfully() {
        UUID pixKeyId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);

        PixKey existingPixKey = new PixKey(
                pixKeyId, KeyType.CPF, "12345678901", AccountType.CORRENTE,
                1234, 98765432, "João",
                "Silva", createdAt, true, null, true);

        PixKey updatedPixKey = new PixKey(
                pixKeyId, KeyType.CPF, "12345678901", AccountType.POUPANCA,
                4321, 87654321, "Maria", "Santos",
                createdAt, true, null, true);

        when(updatePixKeyUseCase.execute(pixKeyId, updatedPixKey)).thenReturn(updatedPixKey);

        PixKey result = updatePixKeyUseCase.execute(pixKeyId, updatedPixKey);

        assertNotNull(result);
        assertEquals("Maria", result.accountHolderName());
        assertEquals(AccountType.POUPANCA, result.accountType());
        verify(updatePixKeyUseCase, times(1)).execute(pixKeyId, updatedPixKey);
    }
}