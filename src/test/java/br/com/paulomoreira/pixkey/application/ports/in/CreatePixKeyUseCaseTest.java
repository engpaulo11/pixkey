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
class CreatePixKeyUseCaseTest {

    @Mock
    private CreatePixKeyUseCase createPixKeyUseCase;

    @Test
    void shouldCreatePixKeySuccessfully() {
        PixKey inputPixKey = new PixKey(
                null, KeyType.CPF, "12345678901", AccountType.CORRENTE,
                1234, 98765432, "João", "Silva",
                null, true, null, true);

        PixKey expectedPixKey = new PixKey(
                UUID.randomUUID(), KeyType.CPF, "12345678901", AccountType.CORRENTE,
                1234, 98765432, "João",
                "Silva", LocalDateTime.now(), true, null, true);

        when(createPixKeyUseCase.execute(inputPixKey)).thenReturn(expectedPixKey);


        PixKey result = createPixKeyUseCase.execute(inputPixKey);

        assertNotNull(result);
        assertNotNull(result.id());
        assertEquals("12345678901", result.keyValue());
        verify(createPixKeyUseCase, times(1)).execute(inputPixKey);
    }
}