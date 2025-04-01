package br.com.paulomoreira.pixkey.application.usecases;

import br.com.paulomoreira.pixkey.application.ports.out.PixKeyRepository;
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
class GetPixKeyUseCaseImplTest {

    @Mock
    private PixKeyRepository repository;

    @InjectMocks
    private GetPixKeyUseCaseImpl useCase;

    @Test
    void shouldReturnPixKeyWhenFound() {
        UUID id = UUID.randomUUID();
        PixKey pixKey = new PixKey(id, KeyType.CELULAR, "+5521994827834", AccountType.CORRENTE,
                1234, 56789012, "Paulo",
                "Moreira", null, true, null, true);
        when(repository.findById(id)).thenReturn(Optional.of(pixKey));

        Optional<PixKey> result = useCase.execute(id);
        assertTrue(result.isPresent());
        assertEquals(pixKey, result.get());
        verify(repository).findById(id);
    }

    @Test
    void shouldReturnEmptyWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        Optional<PixKey> result = useCase.execute(id);
        assertFalse(result.isPresent());
        verify(repository).findById(id);
    }
}
