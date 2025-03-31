package br.com.paulomoreira.pixkey.application.usecases;

import br.com.paulomoreira.pixkey.application.ports.in.UpdatePixKeyUseCase;
import br.com.paulomoreira.pixkey.application.ports.out.PixKeyRepository;
import br.com.paulomoreira.pixkey.domain.model.PixKey;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UpdatePixKeyUseCaseImpl implements UpdatePixKeyUseCase {

    private final PixKeyRepository repository;

    public UpdatePixKeyUseCaseImpl(PixKeyRepository repository) {
        this.repository = repository;
    }

    @Override
    public PixKey execute(UUID id, PixKey pixKey) {
        return repository.save(pixKey);
    }
}