package br.com.paulomoreira.pixkey.application.ports.in;

import java.time.LocalDateTime;

public record SearchPixKeysQuery(
        String keyType,
        Integer branchNumber,
        Integer accountNumber,
        String accountHolderName,
        LocalDateTime createdAt,
        LocalDateTime deactivatedAt
) {}