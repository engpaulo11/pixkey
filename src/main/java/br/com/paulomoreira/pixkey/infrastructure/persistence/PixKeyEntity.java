package br.com.paulomoreira.pixkey.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pix_keys")
@Data
@NoArgsConstructor
public class PixKeyEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "key_value", nullable = false, length = 77)
    private String keyValue;

    @Column(name = "type", nullable = false, length = 9)
    private String type;

    @Column(name = "account_type", nullable = false, length = 10)
    private String accountType;

    @Column(name = "branch_number", nullable = false)
    private Integer branchNumber;

    @Column(name = "account_number", nullable = false)
    private Integer accountNumber;

    @Column(name = "account_holder_name", nullable = false, length = 30)
    private String accountHolderName;

    @Column(name = "account_holder_last_name", length = 45)
    private String accountHolderLastName;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "deactivated_at")
    private LocalDateTime deactivatedAt;

    public PixKeyEntity(UUID id, String keyValue, String type, String accountType,
                        Integer branchNumber, Integer accountNumber, String accountHolderName,
                        String accountHolderLastName, LocalDateTime createdAt, Boolean active,
                        LocalDateTime deactivatedAt) {
        this.id = id;
        this.keyValue = keyValue;
        this.type = type;
        this.accountType = accountType;
        this.branchNumber = branchNumber;
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.accountHolderLastName = accountHolderLastName;
        this.createdAt = createdAt;
        this.active = active;
        this.deactivatedAt = deactivatedAt;
    }

    public boolean isActive() {
        return active;
    }
}