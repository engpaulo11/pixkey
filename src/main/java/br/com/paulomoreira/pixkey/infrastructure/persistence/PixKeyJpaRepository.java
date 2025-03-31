package br.com.paulomoreira.pixkey.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface PixKeyJpaRepository extends JpaRepository<PixKeyEntity, UUID>, JpaSpecificationExecutor<PixKeyEntity> {
    boolean existsByKeyValueAndActiveTrue(String keyValue);
    int countByBranchNumberAndAccountNumberAndActiveTrue(int branchNumber, int accountNumber);
}