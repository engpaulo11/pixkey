package br.com.paulomoreira.pixkey.adapters.out.persistence;


import br.com.paulomoreira.pixkey.application.ports.in.SearchPixKeysQuery;
import br.com.paulomoreira.pixkey.domain.model.AccountType;
import br.com.paulomoreira.pixkey.domain.model.KeyType;
import br.com.paulomoreira.pixkey.domain.model.PixKey;
import br.com.paulomoreira.pixkey.infrastructure.persistence.PixKeyEntity;
import br.com.paulomoreira.pixkey.infrastructure.persistence.PixKeyJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PixKeyRepositoryImplTest {

    @Mock
    private PixKeyJpaRepository jpaRepository;

    @InjectMocks
    private PixKeyRepositoryImpl pixKeyRepository;

    private static final UUID TEST_ID = UUID.randomUUID();
    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final PixKey TEST_PIX_KEY = new PixKey(
            TEST_ID,
            KeyType.CPF,
            "12345678901",
            AccountType.CORRENTE,
            1234,
            98765432,
            "João",
            "Silva",
            NOW,
            true,
            null,
            true
    );

    private static final PixKeyEntity TEST_PIX_KEY_ENTITY = new PixKeyEntity();

    @BeforeEach
    void setUp() {
        // Configuração da entidade de teste
        TEST_PIX_KEY_ENTITY.setId(TEST_ID);
        TEST_PIX_KEY_ENTITY.setKeyValue("12345678901");
        TEST_PIX_KEY_ENTITY.setType("CPF");
        TEST_PIX_KEY_ENTITY.setAccountType("CORRENTE");
        TEST_PIX_KEY_ENTITY.setBranchNumber(1234);
        TEST_PIX_KEY_ENTITY.setAccountNumber(98765432);
        TEST_PIX_KEY_ENTITY.setAccountHolderName("João");
        TEST_PIX_KEY_ENTITY.setAccountHolderLastName("Silva");
        TEST_PIX_KEY_ENTITY.setCreatedAt(NOW);
        TEST_PIX_KEY_ENTITY.setActive(true);
        TEST_PIX_KEY_ENTITY.setDeactivatedAt(null);
    }

    @Test
    @DisplayName("Deve salvar uma chave PIX com sucesso")
    void shouldSavePixKeySuccessfully() {
        when(jpaRepository.save(any(PixKeyEntity.class))).thenReturn(TEST_PIX_KEY_ENTITY);

        PixKey savedPixKey = pixKeyRepository.save(TEST_PIX_KEY);

        assertNotNull(savedPixKey);
        assertEquals(TEST_ID, savedPixKey.id());
        verify(jpaRepository).save(any(PixKeyEntity.class));
    }

    @Test
    @DisplayName("Deve encontrar uma chave PIX por ID")
    void shouldFindPixKeyById() {
        when(jpaRepository.findById(TEST_ID)).thenReturn(Optional.of(TEST_PIX_KEY_ENTITY));

        Optional<PixKey> foundPixKey = pixKeyRepository.findById(TEST_ID);

        assertTrue(foundPixKey.isPresent());
        assertEquals(TEST_ID, foundPixKey.get().id());
    }

    @Test
    @DisplayName("Deve retornar vazio quando chave não encontrada por ID")
    void shouldReturnEmptyWhenPixKeyNotFoundById() {
        when(jpaRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        Optional<PixKey> foundPixKey = pixKeyRepository.findById(TEST_ID);

        assertTrue(foundPixKey.isEmpty());
    }

    @Test
    @DisplayName("Deve verificar existência de chave por valor")
    void shouldCheckKeyValueExistence() {
        when(jpaRepository.existsByKeyValueAndActiveTrue("12345678901")).thenReturn(true);

        boolean exists = pixKeyRepository.existsByKeyValue("12345678901");

        assertTrue(exists);
        verify(jpaRepository).existsByKeyValueAndActiveTrue("12345678901");
    }

    @Test
    @DisplayName("Deve contar chaves por conta")
    void shouldCountKeysByAccount() {
        when(jpaRepository.countByBranchNumberAndAccountNumberAndActiveTrue(1234, 98765432)).thenReturn(3);

        int count = pixKeyRepository.countByAccount(1234, 98765432);

        assertEquals(3, count);
        verify(jpaRepository).countByBranchNumberAndAccountNumberAndActiveTrue(1234, 98765432);
    }

    @Test
    @DisplayName("Deve pesquisar chaves com filtros")
    void shouldSearchPixKeysWithFilters() {
        // Configuração
        Pageable pageable = PageRequest.of(0, 10);
        SearchPixKeysQuery query = new SearchPixKeysQuery(
                KeyType.CPF.toString(),
                1234,
                98765432,
                "João",
                NOW,
                null
        );

        when(jpaRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(TEST_PIX_KEY_ENTITY)));

        // Execução
        Page<PixKey> result = pixKeyRepository.search(query, pageable);

        // Verificação
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(TEST_ID, result.getContent().get(0).id());
        verify(jpaRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("Deve pesquisar chaves com filtros usando método findByFilters")
    void shouldFindByFilters() {
        // Configuração
        Pageable pageable = PageRequest.of(0, 10);
        when(jpaRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(TEST_PIX_KEY_ENTITY)));

        // Execução
        Page<PixKey> result = pixKeyRepository.findByFilters(
                "CPF",
                1234,
                98765432,
                "João",
                NOW,
                null,
                pageable
        );

        // Verificação
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(TEST_ID, result.getContent().get(0).id());
        verify(jpaRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("Deve converter corretamente de PixKey para PixKeyEntity")
    void shouldConvertPixKeyToEntity() {
        PixKeyEntity entity = pixKeyRepository.toEntity(TEST_PIX_KEY);

        assertAll("Conversão para entidade",
                () -> assertEquals(TEST_ID, entity.getId()),
                () -> assertEquals("12345678901", entity.getKeyValue()),
                () -> assertEquals("CPF", entity.getType()),
                () -> assertEquals("CORRENTE", entity.getAccountType()),
                () -> assertEquals(1234, entity.getBranchNumber()),
                () -> assertEquals(98765432, entity.getAccountNumber()),
                () -> assertEquals("João", entity.getAccountHolderName()),
                () -> assertEquals("Silva", entity.getAccountHolderLastName()),
                () -> assertEquals(NOW, entity.getCreatedAt()),
                () -> assertTrue(entity.isActive()),
                () -> assertNull(entity.getDeactivatedAt())
        );
    }

    @Test
    @DisplayName("Deve converter corretamente de PixKeyEntity para PixKey")
    void shouldConvertEntityToPixKey() {
        PixKey domain = pixKeyRepository.toDomain(TEST_PIX_KEY_ENTITY);

        assertAll("Conversão para domínio",
                () -> assertEquals(TEST_ID, domain.id()),
                () -> assertEquals("12345678901", domain.keyValue()),
                () -> assertEquals(KeyType.CPF, domain.type()),
                () -> assertEquals(AccountType.CORRENTE, domain.accountType()),
                () -> assertEquals(1234, domain.branchNumber()),
                () -> assertEquals(98765432, domain.accountNumber()),
                () -> assertEquals("João", domain.accountHolderName()),
                () -> assertEquals("Silva", domain.accountHolderLastName()),
                () -> assertEquals(NOW, domain.createdAt()),
                () -> assertTrue(domain.active()),
                () -> assertNull(domain.deactivatedAt())
        );
    }
}
