package br.com.paulomoreira.pixkey.application.usecases;

import br.com.paulomoreira.pixkey.application.ports.in.SearchPixKeysQuery;
import br.com.paulomoreira.pixkey.application.ports.in.SearchPixKeysUseCase;
import br.com.paulomoreira.pixkey.application.ports.out.PixKeyRepository;
import br.com.paulomoreira.pixkey.domain.exceptions.InvalidFilterCombinationException;
import br.com.paulomoreira.pixkey.domain.model.KeyType;
import br.com.paulomoreira.pixkey.domain.model.PixKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SearchPixKeysUseCaseImpl implements SearchPixKeysUseCase {

    private final PixKeyRepository repository;

    public SearchPixKeysUseCaseImpl(PixKeyRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<PixKey> execute(SearchPixKeysQuery query, Pageable pageable) {

        if (query.createdAt() != null && query.deactivatedAt() != null) {
            throw new InvalidFilterCombinationException("Cannot combine dataInclusao and dataInativacao filters");
        }

        if (query.keyType() == null && query.branchNumber() == null &&
                query.accountNumber() == null && query.accountHolderName() == null &&
                query.createdAt() == null && query.deactivatedAt() == null) {
            throw new InvalidFilterCombinationException("At least one filter must be provided");
        }

        KeyType keyType = null;
        if (query.keyType() != null) {
            try {
                keyType = KeyType.valueOf(query.keyType());
            } catch (IllegalArgumentException e) {
                throw new InvalidFilterCombinationException("Invalid tipoChave: " + query.keyType());
            }
        }

        return repository.search(query, pageable);
    }
}
