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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdatePixKeyUseCaseImplTest {

    @Mock
    private PixKeyRepository repository;

    @InjectMocks
    private UpdatePixKeyUseCaseImpl useCase;

    @Test
    void shouldUpdatePixKeySuccessfully() {
        UUID id = UUID.randomUUID();
        PixKey updatedPixKey = new PixKey(id, KeyType.CELULAR, "+5521994827834", AccountType.POUPANCA,
                4321, 12345678, "Paulo", "Silva",
                null, true, null, true);
        when(repository.save(updatedPixKey)).thenReturn(updatedPixKey);

        PixKey result = useCase.execute(id, updatedPixKey);
        assertEquals(updatedPixKey, result);
        verify(repository).save(updatedPixKey);
    }
}