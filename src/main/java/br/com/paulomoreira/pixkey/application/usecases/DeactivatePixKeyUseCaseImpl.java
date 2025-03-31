package br.com.paulomoreira.pixkey.application.usecases;

import br.com.paulomoreira.pixkey.application.ports.in.DeactivatePixKeyUseCase;
import br.com.paulomoreira.pixkey.application.ports.out.PixKeyRepository;
import br.com.paulomoreira.pixkey.domain.exceptions.KeyAlreadyInactiveException;
import br.com.paulomoreira.pixkey.domain.exceptions.KeyNotFoundException;
import br.com.paulomoreira.pixkey.domain.model.PixKey;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DeactivatePixKeyUseCaseImpl implements DeactivatePixKeyUseCase {

    private final PixKeyRepository repository;

    public DeactivatePixKeyUseCaseImpl(PixKeyRepository repository) {
        this.repository = repository;
    }

    @Override
    public PixKey execute(UUID id) {
        PixKey pixKey = repository.findById(id)
                .orElseThrow(() -> new KeyNotFoundException(id));
        if (!pixKey.active()) {
            throw new KeyAlreadyInactiveException(id);
        }
        pixKey = pixKey.withActive(false);
        return repository.save(pixKey);
    }
}