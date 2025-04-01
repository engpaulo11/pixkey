package br.com.paulomoreira.pixkey.adapters.in.rest;

import br.com.paulomoreira.pixkey.adapters.in.dto.request.CreatePixKeyRequest;
import br.com.paulomoreira.pixkey.adapters.in.dto.request.UpdatePixKeyRequest;
import br.com.paulomoreira.pixkey.adapters.in.dto.response.PixKeyResponse;
import br.com.paulomoreira.pixkey.application.ports.in.*;
import br.com.paulomoreira.pixkey.domain.exceptions.InvalidFilterCombinationException;
import br.com.paulomoreira.pixkey.domain.exceptions.KeyAlreadyInactiveException;
import br.com.paulomoreira.pixkey.domain.exceptions.KeyNotFoundException;
import br.com.paulomoreira.pixkey.domain.model.PixKey;
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
    public ResponseEntity<PixKeyResponse> createPixKey(@Valid @RequestBody CreatePixKeyRequest request) {
        PixKey createdKey = createPixKeyUseCase.execute(request.toDomain());
        return ResponseEntity.ok(PixKeyResponse.fromDomain(createdKey));
    }

    @Override
    public ResponseEntity<PixKeyResponse> getPixKeyById(@PathVariable UUID id, @RequestParam(required = false) String filters) {
        if (filters != null && !filters.isEmpty()) {
            throw new InvalidFilterCombinationException("Additional filters not allowed when searching by ID");
        }
        return getPixKeyUseCase.execute(id)
                .map(key -> ResponseEntity.ok(PixKeyResponse.fromDomain(key)))
                .orElseThrow(() -> new KeyNotFoundException(id));
    }

    @Override
    public ResponseEntity<Page<PixKeyResponse>> getPixKeys(
            @RequestParam(required = false) String tipoChave,
            @RequestParam(required = false) Integer agencia,
            @RequestParam(required = false) Integer conta,
            @RequestParam(required = false) String nomeCorrentista,
            @RequestParam(required = false) String dataInclusao,
            @RequestParam(required = false) String dataInativacao,
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
    public ResponseEntity<PixKeyResponse> updatePixKey(@PathVariable UUID id, @Valid @RequestBody UpdatePixKeyRequest request) {
        PixKey existingKey = getPixKeyUseCase.execute(id)
                .orElseThrow(() -> new KeyNotFoundException(id));
        if (!existingKey.active()) {
            throw new KeyAlreadyInactiveException(id);
        }
        PixKey updatedKey = updatePixKeyUseCase.execute(id, request.toDomain(existingKey));
        return ResponseEntity.ok(PixKeyResponse.fromDomain(updatedKey));
    }

    @Override
    public ResponseEntity<PixKeyResponse> deactivatePixKey(@PathVariable UUID id) {
        PixKey existingKey = getPixKeyUseCase.execute(id)
                .orElseThrow(() -> new KeyNotFoundException(id));
        if (!existingKey.active()) {
            throw new KeyAlreadyInactiveException(id);
        }
        PixKey deactivatedKey = deactivatePixKeyUseCase.execute(id);
        return ResponseEntity.ok(PixKeyResponse.fromDomain(deactivatedKey));
    }
}