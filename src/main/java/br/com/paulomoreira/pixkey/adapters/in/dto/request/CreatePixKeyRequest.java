package br.com.paulomoreira.pixkey.adapters.in.dto.request;

import br.com.paulomoreira.pixkey.domain.model.AccountType;
import br.com.paulomoreira.pixkey.domain.model.KeyType;
import br.com.paulomoreira.pixkey.domain.model.PixKey;
import jakarta.validation.constraints.*;

public record CreatePixKeyRequest(

        @NotNull KeyType tipoChave,
        @NotBlank @Size(max = 77) String valorChave,
        @NotNull AccountType tipoConta,
        @NotNull @Min(1000) @Max(9999) Integer numeroAgencia,
        @NotNull @Min(10000000) @Max(99999999) Integer numeroConta,
        @NotBlank @Size(max = 30) String nomeCorrentista,
        @Size(max = 45) String sobrenomeCorrentista,
        @NotNull(message = "Deve indicar se é pessoa física ou jurídica")
        Boolean isPessoaFisica
) {
    public PixKey toDomain() {
        validate();
        return new PixKey(
                null, tipoChave, valorChave, tipoConta, numeroAgencia, numeroConta,
                nomeCorrentista, sobrenomeCorrentista, null, true, null, isPessoaFisica
        );
    }

    private void validate() {
        if (isPessoaFisica && tipoChave == KeyType.CNPJ) {
            throw new IllegalArgumentException("Pessoa física não pode cadastrar chave CNPJ");
        }

        if (!isPessoaFisica && tipoChave == KeyType.CPF) {
            throw new IllegalArgumentException("Pessoa física não pode cadastrar chave CPF");
        }
    }
}