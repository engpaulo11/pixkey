package br.com.paulomoreira.pixkey.adapters.in.dto.response;

import br.com.paulomoreira.pixkey.domain.model.PixKey;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.format.DateTimeFormatter;

@Schema(description = "Resposta com os detalhes de uma chave Pix")
public record PixKeyResponse(
        @Schema(description = "ID da chave Pix", example = "550e8400-e29b-41d4-a716-446655440000")
        @JsonProperty("id")
        String id,

        @Schema(description = "Tipo da chave", example = "CPF")
        @JsonProperty("tipoChave")
        String keyType,

        @Schema(description = "Valor da chave", example = "12345678901")
        @JsonProperty("valorChave")
        String keyValue,

        @Schema(description = "Tipo da conta", example = "CORRENTE")
        @JsonProperty("tipoConta")
        String accountType,

        @Schema(description = "Número da agência", example = "1234")
        @JsonProperty("numeroAgencia")
        Integer branchNumber,

        @Schema(description = "Número da conta", example = "12345678")
        @JsonProperty("numeroConta")
        Integer accountNumber,

        @Schema(description = "Nome do correntista", example = "Paulo")
        @JsonProperty("nomeCorrentista")
        String accountHolderName,

        @Schema(description = "Sobrenome do correntista", example = "Moreira")
        @JsonProperty("sobrenomeCorrentista")
        String accountHolderLastName,


        @Schema(description = "Data de criação", example = "2025-03-31T10:00:00")
        @JsonProperty("dataHoraInclusao")
        String createdAt,

        @Schema(description = "Data de inativação (se aplicável)", example = "2025-03-31T12:00:00")
        @JsonProperty("dataHoraInativacao")
        String deactivatedAt,

        @Schema(description = "Indica se é pessoa física", example = "true")
        boolean isPessoaFisica
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
                pixKey.deactivatedAt() != null ? pixKey.deactivatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null,
                pixKey.isLegalPerson()
        );

    }
}