package br.com.paulomoreira.pixkey.application.ports.in;

import br.com.paulomoreira.pixkey.domain.model.PixKey;
import java.util.Optional;
import java.util.UUID;

public interface GetPixKeyUseCase {
    Optional<PixKey> execute(UUID id);
}