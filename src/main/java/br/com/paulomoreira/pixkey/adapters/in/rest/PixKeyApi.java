package br.com.paulomoreira.pixkey.adapters.in.rest;

import br.com.paulomoreira.pixkey.adapters.in.dto.request.CreatePixKeyRequest;
import br.com.paulomoreira.pixkey.adapters.in.dto.request.UpdatePixKeyRequest;
import br.com.paulomoreira.pixkey.adapters.in.dto.response.PixKeyResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

public interface PixKeyApi {
    @PostMapping
    ResponseEntity<PixKeyResponse> createPixKey(@RequestBody CreatePixKeyRequest request);

    @GetMapping("/{id}")
    ResponseEntity<PixKeyResponse> getPixKeyById(@PathVariable UUID id, @RequestParam(required = false) String filters);

    @GetMapping
    ResponseEntity<Page<PixKeyResponse>> getPixKeys(
            @RequestParam(required = false) String tipoChave,
            @RequestParam(required = false) Integer agencia,
            @RequestParam(required = false) Integer conta,
            @RequestParam(required = false) String nomeCorrentista,
            @RequestParam(required = false) String dataInclusao,
            @RequestParam(required = false) String dataInativacao,
            Pageable pageable);

    @PutMapping("/{id}")
    ResponseEntity<PixKeyResponse> updatePixKey(@PathVariable UUID id, @RequestBody UpdatePixKeyRequest request);

    @DeleteMapping("/{id}")
    ResponseEntity<PixKeyResponse> deactivatePixKey(@PathVariable UUID id);
}