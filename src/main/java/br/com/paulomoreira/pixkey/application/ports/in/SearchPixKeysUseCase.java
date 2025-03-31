package br.com.paulomoreira.pixkey.application.ports.in;

import br.com.paulomoreira.pixkey.domain.model.PixKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchPixKeysUseCase {
    Page<PixKey> execute(SearchPixKeysQuery query, Pageable pageable);
}