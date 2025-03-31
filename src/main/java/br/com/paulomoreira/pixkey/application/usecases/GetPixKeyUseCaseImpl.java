package br.com.paulomoreira.pixkey.application.usecases;

import br.com.paulomoreira.pixkey.application.ports.in.GetPixKeyUseCase;
import br.com.paulomoreira.pixkey.application.ports.out.PixKeyRepository;
import br.com.paulomoreira.pixkey.domain.model.PixKey;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class GetPixKeyUseCaseImpl implements GetPixKeyUseCase {

    private final PixKeyRepository repository;

    public GetPixKeyUseCaseImpl(PixKeyRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<PixKey> execute(UUID id) {
        return repository.findById(id);
    }
}