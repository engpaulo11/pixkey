package br.com.paulomoreira.pixkey.integration;

import br.com.paulomoreira.pixkey.adapters.inbound.dto.request.CreatePixKeyRequest;
import br.com.paulomoreira.pixkey.adapters.inbound.dto.response.PixKeyResponse;
import br.com.paulomoreira.pixkey.adapters.inbound.rest.PixKeyController;
import br.com.paulomoreira.pixkey.domain.exceptions.KeyAlreadyInactiveException;
import br.com.paulomoreira.pixkey.domain.exceptions.KeyNotFoundException;
import br.com.paulomoreira.pixkey.domain.model.AccountType;
import br.com.paulomoreira.pixkey.domain.model.KeyType;
import br.com.paulomoreira.pixkey.infrastructure.persistence.PixKeyEntity;
import br.com.paulomoreira.pixkey.infrastructure.persistence.PixKeyJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class DeactivatePixKeyIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("pixkey-test")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("init.sql")
            .withStartupTimeout(Duration.ofSeconds(60));

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () ->
                String.format("jdbc:postgresql://%s:%d/%s",
                        postgres.getHost(),
                        postgres.getFirstMappedPort(),
                        postgres.getDatabaseName()));
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    @Autowired
    private PixKeyController pixKeyController;

    @Autowired
    private PixKeyJpaRepository pixKeyJpaRepository;

    @BeforeEach
    @Transactional
    void cleanDatabase() {
        pixKeyJpaRepository.deleteAll();
        pixKeyJpaRepository.flush();
    }

    private static final CreatePixKeyRequest VALID_REQUEST = new CreatePixKeyRequest(
            KeyType.CPF, "84315720003", AccountType.CORRENTE, 1234, 98765432, "João", "Silva"
    );

    // Trecho relevante
    @Test
    @Transactional
    void deactivatePixKey_ShouldReturnOk_WhenKeyExistsAndActive() {
        ResponseEntity<PixKeyResponse> createResponse = pixKeyController.createPixKey(VALID_REQUEST);
        UUID id = UUID.fromString(createResponse.getBody().id());

        ResponseEntity<PixKeyResponse> deactivateResponse = pixKeyController.deactivatePixKey(id);
        assertEquals(HttpStatus.OK, deactivateResponse.getStatusCode());

        PixKeyResponse deactivatedKey = deactivateResponse.getBody();
        assertNotNull(deactivatedKey, "O response body não deveria ser nulo");
        assertEquals(id.toString(), deactivatedKey.id());
        assertNotNull(deactivatedKey.dataHoraInativacao(), "dataHoraInativacao deveria ser preenchida pelo código");

        PixKeyEntity entity = pixKeyJpaRepository.findById(id).orElseThrow();
        assertFalse(entity.isActive(), "A chave deveria estar inativa");
        assertNotNull(entity.getDeactivatedAt(), "deactivated_at deveria ser preenchido pelo código");
    }

    @Test
    @Transactional
    void deactivatePixKey_ShouldThrowKeyNotFoundException_WhenKeyDoesNotExist() {
        UUID nonExistentId = UUID.randomUUID();
        assertThrows(
                KeyNotFoundException.class,
                () -> pixKeyController.deactivatePixKey(nonExistentId)
        );
    }

    @Test
    @Transactional
    void deactivatePixKey_ShouldThrowKeyAlreadyInactiveException_WhenKeyIsInactive() {
        ResponseEntity<PixKeyResponse> createResponse = pixKeyController.createPixKey(VALID_REQUEST);
        UUID id = UUID.fromString(createResponse.getBody().id());
        pixKeyController.deactivatePixKey(id);

        assertThrows(
                KeyAlreadyInactiveException.class,
                () -> pixKeyController.deactivatePixKey(id)
        );
    }
}