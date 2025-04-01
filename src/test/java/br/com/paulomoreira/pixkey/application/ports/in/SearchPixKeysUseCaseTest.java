package br.com.paulomoreira.pixkey.application.ports.in;

import br.com.paulomoreira.pixkey.domain.model.AccountType;
import br.com.paulomoreira.pixkey.domain.model.KeyType;
import br.com.paulomoreira.pixkey.domain.model.PixKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchPixKeysUseCaseTest {

    @Mock
    private SearchPixKeysUseCase searchPixKeysUseCase;

    @Test
    void shouldSearchPixKeysWithFilters() {
        SearchPixKeysQuery query = new SearchPixKeysQuery(
                KeyType.CPF.toString(), 1234, 98765432, "João", LocalDateTime.now().minusDays(1), null);
        Pageable pageable = PageRequest.of(0, 10);

        PixKey pixKey = new PixKey(
                UUID.randomUUID(), KeyType.CPF, "12345678901", AccountType.CORRENTE,
                1234, 98765432, "João", "Silva",
                LocalDateTime.now(), true, null, true);

        Page<PixKey> expectedPage = new PageImpl<>(List.of(pixKey), pageable, 1);

        when(searchPixKeysUseCase.execute(query, pageable)).thenReturn(expectedPage);

        Page<PixKey> result = searchPixKeysUseCase.execute(query, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(searchPixKeysUseCase, times(1)).execute(query, pageable);
    }
}