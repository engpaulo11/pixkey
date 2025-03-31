package br.com.paulomoreira.pixkey.application.ports.out;

import br.com.paulomoreira.pixkey.application.ports.in.SearchPixKeysQuery;
import br.com.paulomoreira.pixkey.domain.model.KeyType;
import br.com.paulomoreira.pixkey.domain.model.PixKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface PixKeyRepository {

    PixKey save(PixKey pixKey);

    Optional<PixKey> findById(UUID id);

    boolean existsByKeyValue(String keyValue);

    int countByAccount(int branchNumber, int accountNumber);

    Page<PixKey> search(SearchPixKeysQuery query, Pageable pageable);

    Page<PixKey> findByFilters(String tipoChave, Integer branchNumber, Integer accountNumber,
                               String accountHolderName, LocalDateTime createdAt,
                               LocalDateTime deactivatedAt, Pageable pageable);
}