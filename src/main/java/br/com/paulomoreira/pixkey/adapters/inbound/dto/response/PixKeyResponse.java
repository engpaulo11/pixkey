package br.com.paulomoreira.pixkey.adapters.inbound.dto.response;

import br.com.paulomoreira.pixkey.domain.model.PixKey;
import java.time.format.DateTimeFormatter;

public record PixKeyResponse(
        String id,
        String tipoChave,
        String valorChave,
        String tipoConta,
        Integer numeroAgencia,
        Integer numeroConta,
        String nomeCorrentista,
        String sobrenomeCorrentista,
        String dataHoraInclusao,
        String dataHoraInativacao
) {
    public static PixKeyResponse fromDomain(PixKey pixKey) {
        return new PixKeyResponse(
                pixKey.id().toString(),
                pixKey.type().name(),
                pixKey.keyValue(),
                pixKey.accountType().name(),
                pixKey.branchNumber(),
                pixKey.accountNumber(),
                pixKey.accountHolderName(),
                pixKey.accountHolderLastName(),
                pixKey.createdAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                pixKey.deactivatedAt() != null ? pixKey.deactivatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null
        );
    }
}