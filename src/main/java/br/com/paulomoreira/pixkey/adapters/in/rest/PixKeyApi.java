package br.com.paulomoreira.pixkey.adapters.in.rest;

import br.com.paulomoreira.pixkey.adapters.in.dto.request.CreatePixKeyRequest;
import br.com.paulomoreira.pixkey.adapters.in.dto.request.UpdatePixKeyRequest;
import br.com.paulomoreira.pixkey.adapters.in.dto.response.PixKeyResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Pix Keys", description = "API para gerenciamento de chaves Pix")
public interface PixKeyApi {

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
    @PostMapping
    ResponseEntity<PixKeyResponse> createPixKey(
            @Parameter(description = "Dados da nova chave Pix, incluindo se é PF ou PJ", required = true)
            @RequestBody CreatePixKeyRequest request
    );

    @Operation(summary = "Busca uma chave Pix por ID", description = "Retorna os detalhes de uma chave Pix específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chave Pix encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PixKeyResponse.class))),
            @ApiResponse(responseCode = "404", description = "Chave Pix não encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    ResponseEntity<PixKeyResponse> getPixKeyById(
            @Parameter(description = "ID da chave Pix", required = true, example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID id,
            @Parameter(description = "Filtros adicionais (opcional)", example = "active=true") @RequestParam(required = false) String filters
    );

    @Operation(summary = "Lista chaves Pix com filtros e paginação", description = "Retorna uma lista paginada de chaves Pix.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de chaves Pix retornada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos", content = @Content)
    })
    @GetMapping
    ResponseEntity<Page<PixKeyResponse>> getPixKeys(
            @Parameter(description = "Tipo da chave", example = "CPF") @RequestParam(required = false) String tipoChave,
            @Parameter(description = "Número da agência", example = "1234") @RequestParam(required = false) Integer agencia,
            @Parameter(description = "Número da conta", example = "12345678") @RequestParam(required = false) Integer conta,
            @Parameter(description = "Nome do correntista", example = "Paulo") @RequestParam(required = false) String nomeCorrentista,
            @Parameter(description = "Data de inclusão (ISO)", example = "2025-03-31") @RequestParam(required = false) String dataInclusao,
            @Parameter(description = "Data de inativação (ISO)", example = "2025-03-31") @RequestParam(required = false) String dataInativacao,
            @Parameter(description = "Parâmetros de paginação") Pageable pageable
    );

    @Operation(summary = "Atualiza uma chave Pix", description = "Atualiza os dados de uma chave Pix (exceto tipo e valor).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chave Pix atualizada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PixKeyResponse.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content),
            @ApiResponse(responseCode = "404", description = "Chave Pix não encontrada", content = @Content)
    })
    @PutMapping("/{id}")
    ResponseEntity<PixKeyResponse> updatePixKey(
            @Parameter(description = "ID da chave Pix", required = true, example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID id,
            @Parameter(description = "Dados atualizados", required = true) @RequestBody UpdatePixKeyRequest request
    );

    @Operation(summary = "Inativa uma chave Pix", description = "Marca uma chave Pix como inativa.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chave Pix inativada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PixKeyResponse.class))),
            @ApiResponse(responseCode = "404", description = "Chave Pix não encontrada", content = @Content)
    })
    @DeleteMapping("/{id}")
    ResponseEntity<PixKeyResponse> deactivatePixKey(
            @Parameter(description = "ID da chave Pix", required = true, example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID id
    );
}