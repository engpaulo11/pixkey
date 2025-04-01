package br.com.paulomoreira.pixkey.adapters.in.rest;

import br.com.paulomoreira.pixkey.adapters.in.dto.request.CreatePixKeyRequest;
import br.com.paulomoreira.pixkey.adapters.in.dto.response.PixKeyResponse;
import br.com.paulomoreira.pixkey.application.ports.in.*;
import br.com.paulomoreira.pixkey.domain.model.AccountType;
import br.com.paulomoreira.pixkey.domain.model.KeyType;
import br.com.paulomoreira.pixkey.domain.model.PixKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PixKeyControllerTest {

    @Mock
    private CreatePixKeyUseCase createPixKeyUseCase;

    @Mock
    private GetPixKeyUseCase getPixKeyUseCase;

    @InjectMocks
    private PixKeyController pixKeyController;

    private UUID pixKeyId;
    private PixKey pixKey;
    private CreatePixKeyRequest createPixKeyRequest;

    @BeforeEach
    void setUp() {
        pixKeyId = UUID.randomUUID();
        pixKey = new PixKey(
                pixKeyId, KeyType.EMAIL, "test@example.com", AccountType.CORRENTE,
                1234, 56789012, "John", "Doe",
                null, true, null, true
        );
        createPixKeyRequest = new CreatePixKeyRequest(
                KeyType.EMAIL, "test@example.com", AccountType.CORRENTE,
                1234, 56789012, "John", "Doe",
                true
        );
    }

    @Test
    void shouldCreatePixKeySuccessfully() {
        when(createPixKeyUseCase.execute(any())).thenReturn(pixKey);

        ResponseEntity<PixKeyResponse> response = pixKeyController.createPixKey(createPixKeyRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(pixKeyId, UUID.fromString(response.getBody().id()));
    }

    @Test
    void shouldReturnPixKeyById() {
        when(getPixKeyUseCase.execute(pixKeyId)).thenReturn(Optional.of(pixKey));

        ResponseEntity<PixKeyResponse> response = pixKeyController.getPixKeyById(pixKeyId, null);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(pixKeyId, UUID.fromString(response.getBody().id()));
    }

    @Test
    void shouldThrowExceptionWhenPixKeyNotFound() {
        when(getPixKeyUseCase.execute(pixKeyId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> pixKeyController.getPixKeyById(pixKeyId, null));
    }
}