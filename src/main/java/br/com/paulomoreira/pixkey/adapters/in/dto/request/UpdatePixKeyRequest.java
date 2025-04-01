package br.com.paulomoreira.pixkey.adapters.in.dto.request;

import br.com.paulomoreira.pixkey.domain.model.AccountType;
import br.com.paulomoreira.pixkey.domain.model.PixKey;
import jakarta.validation.constraints.*;

public record UpdatePixKeyRequest(
        @NotNull AccountType tipoConta,
        @NotNull @Min(1000) @Max(9999) Integer numeroAgencia,
        @NotNull @Min(10000000) @Max(99999999) Integer numeroConta,
        @NotBlank @Size(max = 30) String nomeCorrentista,
        @Size(max = 45) String sobrenomeCorrentista
) {
    public PixKey toDomain(PixKey existingKey) {
        return new PixKey(
                existingKey.id(), existingKey.type(), existingKey.keyValue(),
                tipoConta, numeroAgencia, numeroConta, nomeCorrentista, sobrenomeCorrentista,
                existingKey.createdAt(), existingKey.active(), existingKey.deactivatedAt(),
                existingKey.isLegalPerson()
        );
    }
}