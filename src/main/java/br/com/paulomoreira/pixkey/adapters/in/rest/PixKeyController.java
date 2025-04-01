package br.com.paulomoreira.pixkey.adapters.in.rest;

import br.com.paulomoreira.pixkey.adapters.in.dto.request.CreatePixKeyRequest;
import br.com.paulomoreira.pixkey.adapters.in.dto.request.UpdatePixKeyRequest;
import br.com.paulomoreira.pixkey.adapters.in.dto.response.PixKeyResponse;
import br.com.paulomoreira.pixkey.application.ports.in.*;
import br.com.paulomoreira.pixkey.domain.exceptions.InvalidFilterCombinationException;
import br.com.paulomoreira.pixkey.domain.exceptions.KeyAlreadyInactiveException;
import br.com.paulomoreira.pixkey.domain.exceptions.KeyNotFoundException;
import br.com.paulomoreira.pixkey.domain.model.PixKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pix-keys")
@Tag(name = "Pix Keys", description = "API para gerenciamento de chaves Pix")
public class PixKeyController implements PixKeyApi {

    private final CreatePixKeyUseCase createPixKeyUseCase;
    private final GetPixKeyUseCase getPixKeyUseCase;
    private final SearchPixKeysUseCase searchPixKeysUseCase;
    private final UpdatePixKeyUseCase updatePixKeyUseCase;
    private final DeactivatePixKeyUseCase deactivatePixKeyUseCase;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PixKeyController(
            CreatePixKeyUseCase createPixKeyUseCase,
            GetPixKeyUseCase getPixKeyUseCase,
            SearchPixKeysUseCase searchPixKeysUseCase,
            UpdatePixKeyUseCase updatePixKeyUseCase,
            DeactivatePixKeyUseCase deactivatePixKeyUseCase) {
        this.createPixKeyUseCase = createPixKeyUseCase;
        this.getPixKeyUseCase = getPixKeyUseCase;
        this.searchPixKeysUseCase = searchPixKeysUseCase;
        this.updatePixKeyUseCase = updatePixKeyUseCase;
        this.deactivatePixKeyUseCase = deactivatePixKeyUseCase;
    }

    @Override
    @Operation(
            summary = "Cria uma nova chave Pix",
            description = "Registra uma nova chave Pix. Pessoa física (PF) tem limite de 5 chaves e não pode usar CNPJ. Pessoa jurídica (PJ) tem limite de 20 chaves."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chave Pix criada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PixKeyResponse.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida (ex.: CNPJ para PF, limite excedido)",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Chave duplicada",
                    content = @Content)
    })
    public ResponseEntity<PixKeyResponse> createPixKey(
            @Parameter(description = "Dados da nova chave Pix, incluindo se é PF ou PJ", required = true)
            @Valid @RequestBody CreatePixKeyRequest request) {
        PixKey createdKey = createPixKeyUseCase.execute(request.toDomain());
        return ResponseEntity.ok(PixKeyResponse.fromDomain(createdKey));
    }

    @Override
    @Operation(
            summary = "Busca uma chave Pix por ID",
            description = "Retorna os detalhes de uma chave Pix específica com base em seu ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chave Pix encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PixKeyResponse.class))),
            @ApiResponse(responseCode = "400", description = "Filtros adicionais não permitidos ao buscar por ID",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Chave Pix não encontrada",
                    content = @Content)
    })
    public ResponseEntity<PixKeyResponse> getPixKeyById(
            @Parameter(description = "ID da chave Pix", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id,
            @Parameter(description = "Filtros adicionais (não permitido)", example = "active=true")
            @RequestParam(required = false) String filters) {
        if (filters != null && !filters.isEmpty()) {
            throw new InvalidFilterCombinationException("Additional filters not allowed when searching by ID");
        }
        return getPixKeyUseCase.execute(id)
                .map(key -> ResponseEntity.ok(PixKeyResponse.fromDomain(key)))
                .orElseThrow(() -> new KeyNotFoundException(id));
    }

    @Override
    @Operation(
            summary = "Lista chaves Pix com filtros e paginação",
            description = "Retorna uma lista paginada de chaves Pix com base em filtros opcionais. Não permite combinar data de inclusão e inativação."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de chaves Pix retornada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Combinação de filtros inválida",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Nenhuma chave encontrada",
                    content = @Content)
    })
    public ResponseEntity<Page<PixKeyResponse>> getPixKeys(
            @Parameter(description = "Tipo da chave (ex.: CPF, CNPJ)", example = "CPF")
            @RequestParam(required = false) String tipoChave,
            @Parameter(description = "Número da agência", example = "1234")
            @RequestParam(required = false) Integer agencia,
            @Parameter(description = "Número da conta", example = "12345678")
            @RequestParam(required = false) Integer conta,
            @Parameter(description = "Nome do correntista", example = "Paulo")
            @RequestParam(required = false) String nomeCorrentista,
            @Parameter(description = "Data de inclusão (dd/MM/yyyy)", example = "31/03/2025")
            @RequestParam(required = false) String dataInclusao,
            @Parameter(description = "Data de inativação (dd/MM/yyyy)", example = "31/03/2025")
            @RequestParam(required = false) String dataInativacao,
            @Parameter(description = "Parâmetros de paginação (ex.: page=0, size=20, sort=createdAt,desc)")
            @PageableDefault(size = 20) Pageable pageable) {

        if (dataInclusao != null && dataInativacao != null) {
            throw new InvalidFilterCombinationException("Cannot filter by both inclusion and deactivation dates");
        }

        SearchPixKeysQuery query = new SearchPixKeysQuery(
                tipoChave != null ? tipoChave.toUpperCase() : null,
                agencia, conta, nomeCorrentista,
                dataInclusao != null ? LocalDate.parse(dataInclusao, DATE_FORMATTER).atStartOfDay() : null,
                dataInativacao != null ? LocalDate.parse(dataInativacao, DATE_FORMATTER).atStartOfDay() : null
        );
        Page<PixKey> keys = searchPixKeysUseCase.execute(query, pageable);

        if (keys.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(keys.map(PixKeyResponse::fromDomain));
    }

    @Override
    @Operation(
            summary = "Atualiza uma chave Pix existente",
            description = "Atualiza os dados de uma chave Pix (exceto tipo e valor) com base em seu ID. Não permite atualizar chaves inativas."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chave Pix atualizada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PixKeyResponse.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida ou chave já inativa",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Chave Pix não encontrada",
                    content = @Content)
    })
    public ResponseEntity<PixKeyResponse> updatePixKey(
            @Parameter(description = "ID da chave Pix", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id,
            @Parameter(description = "Dados atualizados da chave Pix", required = true)
            @Valid @RequestBody UpdatePixKeyRequest request) {
        PixKey existingKey = getPixKeyUseCase.execute(id)
                .orElseThrow(() -> new KeyNotFoundException(id));
        if (!existingKey.active()) {
            throw new KeyAlreadyInactiveException(id);
        }
        PixKey updatedKey = updatePixKeyUseCase.execute(id, request.toDomain(existingKey));
        return ResponseEntity.ok(PixKeyResponse.fromDomain(updatedKey));
    }

    @Override
    @Operation(
            summary = "Inativa uma chave Pix",
            description = "Marca uma chave Pix como inativa com base em seu ID. Não permite inativar chaves já inativas."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chave Pix inativada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PixKeyResponse.class))),
            @ApiResponse(responseCode = "400", description = "Chave já inativa",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Chave Pix não encontrada",
                    content = @Content)
    })
    public ResponseEntity<PixKeyResponse> deactivatePixKey(
            @Parameter(description = "ID da chave Pix", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID id) {
        PixKey existingKey = getPixKeyUseCase.execute(id)
                .orElseThrow(() -> new KeyNotFoundException(id));
        if (!existingKey.active()) {
            throw new KeyAlreadyInactiveException(id);
        }
        PixKey deactivatedKey = deactivatePixKeyUseCase.execute(id);
        return ResponseEntity.ok(PixKeyResponse.fromDomain(deactivatedKey));
    }
}