package br.com.paulomoreira.pixkey.application.ports.in;

import br.com.paulomoreira.pixkey.domain.model.PixKey;

public interface CreatePixKeyUseCase {
    PixKey execute(PixKey pixKey);
}