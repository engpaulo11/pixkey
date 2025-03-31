package br.com.paulomoreira.pixkey.application.usecases;

import br.com.paulomoreira.pixkey.application.ports.in.SearchPixKeysQuery;
import br.com.paulomoreira.pixkey.application.ports.out.PixKeyRepository;
import br.com.paulomoreira.pixkey.domain.exceptions.InvalidFilterCombinationException;
import br.com.paulomoreira.pixkey.domain.model.PixKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchPixKeysUseCaseImplTest {

    @Mock
    private PixKeyRepository repository;

    @InjectMocks
    private SearchPixKeysUseCaseImpl useCase;

    @Test
    void shouldThrowExceptionWhenNoFilterProvided() {
        SearchPixKeysQuery query = new SearchPixKeysQuery(null, null, null, null, null, null);
        Pageable pageable = PageRequest.of(0, 20);

        InvalidFilterCombinationException exception = assertThrows(InvalidFilterCombinationException.class,
                () -> useCase.execute(query, pageable));
        assertEquals("At least one filter must be provided", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCreatedAtAndDeactivatedAtAreCombined() {
        SearchPixKeysQuery query = new SearchPixKeysQuery(null, null, null, null,
                LocalDateTime.now(), LocalDateTime.now());
        Pageable pageable = PageRequest.of(0, 20);

        InvalidFilterCombinationException exception = assertThrows(InvalidFilterCombinationException.class,
                () -> useCase.execute(query, pageable));
        assertEquals("Cannot combine dataInclusao and dataInativacao filters", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenKeyTypeIsInvalid() {
        SearchPixKeysQuery query = new SearchPixKeysQuery("INVALID", null, null, null, null, null);
        Pageable pageable = PageRequest.of(0, 20);

        InvalidFilterCombinationException exception = assertThrows(InvalidFilterCombinationException.class,
                () -> useCase.execute(query, pageable));
        assertEquals("Invalid tipoChave: INVALID", exception.getMessage());
    }

    @Test
    void shouldSearchSuccessfullyWithValidFilter() {
        SearchPixKeysQuery query = new SearchPixKeysQuery("CELULAR", null, null, null, null, null);
        Pageable pageable = PageRequest.of(0, 20);
        Page<PixKey> pageResult = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(repository.search(query, pageable)).thenReturn(pageResult);

        Page<PixKey> result = useCase.execute(query, pageable);
        assertEquals(pageResult, result);
        verify(repository).search(query, pageable);
    }
}