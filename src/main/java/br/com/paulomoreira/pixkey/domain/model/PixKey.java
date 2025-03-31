package br.com.paulomoreira.pixkey.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public record PixKey(
        UUID id,
        KeyType type,
        String keyValue,
        AccountType accountType,
        Integer branchNumber,
        Integer accountNumber,
        String accountHolderName,
        String accountHolderLastName,
        LocalDateTime createdAt,
        boolean active,
        LocalDateTime deactivatedAt
) {

    public PixKey {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        Objects.requireNonNull(keyValue, "keyValue cannot be null");
        Objects.requireNonNull(type, "type cannot be null");
        Objects.requireNonNull(accountType, "accountType cannot be null");
    }

    public PixKey withActive(boolean active) {
        if (this.active == active) {
            return this;
        }
        LocalDateTime newDeactivatedAt = active ? null : LocalDateTime.now();
        return new PixKey(id, type,keyValue, accountType, branchNumber, accountNumber,
                accountHolderName, accountHolderLastName, createdAt, active, newDeactivatedAt);
    }

}