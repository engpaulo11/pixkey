package br.com.paulomoreira.pixkey.adapters.out.persistence;

import br.com.paulomoreira.pixkey.application.ports.in.SearchPixKeysQuery;
import br.com.paulomoreira.pixkey.application.ports.out.PixKeyRepository;
import br.com.paulomoreira.pixkey.domain.model.AccountType;
import br.com.paulomoreira.pixkey.domain.model.KeyType;
import br.com.paulomoreira.pixkey.domain.model.PixKey;
import br.com.paulomoreira.pixkey.infrastructure.persistence.PixKeyEntity;
import br.com.paulomoreira.pixkey.infrastructure.persistence.PixKeyJpaRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PixKeyRepositoryImpl implements PixKeyRepository {

    private final PixKeyJpaRepository jpaRepository;

    public PixKeyRepositoryImpl(PixKeyJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public PixKey save(PixKey pixKey) {
        PixKeyEntity entity = toEntity(pixKey);
        PixKeyEntity savedEntity = jpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<PixKey> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public boolean existsByKeyValue(String keyValue) {
        return jpaRepository.existsByKeyValueAndActiveTrue(keyValue);
    }

    @Override
    public int countByAccount(int branchNumber, int accountNumber) {
        return jpaRepository.countByBranchNumberAndAccountNumberAndActiveTrue(branchNumber, accountNumber);
    }

    @Override
    public Page<PixKey> search(SearchPixKeysQuery query, Pageable pageable) {
        return findByFilters(
                query.keyType(),
                query.branchNumber(),
                query.accountNumber(),
                query.accountHolderName(),
                query.createdAt(),
                query.deactivatedAt(),
                pageable
        );
    }

    @Override
    public Page<PixKey> findByFilters(String tipoChave, Integer branchNumber, Integer accountNumber,
                                      String accountHolderName, LocalDateTime createdAt,
                                      LocalDateTime deactivatedAt, Pageable pageable) {
        Specification<PixKeyEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (tipoChave != null) {
                predicates.add(cb.equal(root.get("type"), tipoChave));
            }
            if (branchNumber != null) {
                predicates.add(cb.equal(root.get("branchNumber"), branchNumber));
            }
            if (accountNumber != null) {
                predicates.add(cb.equal(root.get("accountNumber"), accountNumber));
            }
            if (accountHolderName != null) {
                predicates.add(cb.equal(root.get("accountHolderName"), accountHolderName));
            }
            if (createdAt != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), createdAt));
            }
            if (deactivatedAt != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("deactivatedAt"), deactivatedAt));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<PixKeyEntity> entityPage = jpaRepository.findAll(spec, pageable);
        List<PixKey> pixKeys = entityPage.getContent().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());

        return new PageImpl<>(pixKeys, pageable, entityPage.getTotalElements());
    }

    public PixKeyEntity toEntity(PixKey pixKey) {
        PixKeyEntity entity = new PixKeyEntity();
        entity.setId(pixKey.id());
        entity.setKeyValue(pixKey.keyValue());
        entity.setType(pixKey.type().name());
        entity.setAccountType(pixKey.accountType().name());
        entity.setBranchNumber(pixKey.branchNumber());
        entity.setAccountNumber(pixKey.accountNumber());
        entity.setAccountHolderName(pixKey.accountHolderName());
        entity.setAccountHolderLastName(pixKey.accountHolderLastName());
        entity.setCreatedAt(pixKey.createdAt());
        entity.setActive(pixKey.active());
        entity.setDeactivatedAt(pixKey.deactivatedAt());
        return entity;
    }

    public PixKey toDomain(PixKeyEntity entity) {
        return new PixKey(
                entity.getId(),
                KeyType.valueOf(entity.getType()),
                entity.getKeyValue(),
                AccountType.valueOf(entity.getAccountType()),
                entity.getBranchNumber(),
                entity.getAccountNumber(),
                entity.getAccountHolderName(),
                entity.getAccountHolderLastName(),
                entity.getCreatedAt(),
                entity.isActive(),
                entity.getDeactivatedAt(),
                entity.isLegalPerson()
        );
    }
}