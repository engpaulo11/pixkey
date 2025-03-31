package br.com.paulomoreira.pixkey.application.ports.in;

import br.com.paulomoreira.pixkey.domain.model.PixKey;
import java.util.UUID;

public interface UpdatePixKeyUseCase {
    PixKey execute(UUID id, PixKey pixKey);
}