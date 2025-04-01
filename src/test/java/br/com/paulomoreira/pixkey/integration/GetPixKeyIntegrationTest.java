package br.com.paulomoreira.pixkey.integration;

import br.com.paulomoreira.pixkey.adapters.in.dto.request.CreatePixKeyRequest;
import br.com.paulomoreira.pixkey.adapters.in.dto.response.PixKeyResponse;
import br.com.paulomoreira.pixkey.adapters.in.rest.PixKeyController;
import br.com.paulomoreira.pixkey.domain.exceptions.KeyNotFoundException;
import br.com.paulomoreira.pixkey.domain.model.AccountType;
import br.com.paulomoreira.pixkey.domain.model.KeyType;
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
class GetPixKeyIntegrationTest {

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
   , true );

    @Test
    @Transactional
    void getPixKeyById_ShouldReturnOk_WhenKeyExists() {
        ResponseEntity<PixKeyResponse> createResponse = pixKeyController.createPixKey(VALID_REQUEST);
        UUID id = UUID.fromString(createResponse.getBody().id());

        ResponseEntity<PixKeyResponse> response = pixKeyController.getPixKeyById(id, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        PixKeyResponse pixKey = response.getBody();
        assertEquals(id.toString(), pixKey.id());
        assertEquals("CPF", pixKey.keyType());
        assertEquals("84315720003", pixKey.keyValue());
        assertEquals("CORRENTE", pixKey.accountType());
        assertEquals(1234, pixKey.branchNumber());
        assertEquals(98765432, pixKey.accountNumber());
        assertEquals("João", pixKey.accountHolderName());
        assertEquals("Silva", pixKey.accountHolderLastName());
        assertNotNull(pixKey.createdAt());
        assertNull(pixKey.deactivatedAt());
    }

    @Test
    @Transactional
    void getPixKeyById_ShouldThrowKeyNotFoundException_WhenKeyDoesNotExist() {
        UUID nonExistentId = UUID.randomUUID();
        assertThrows(KeyNotFoundException.class, () -> pixKeyController.getPixKeyById(nonExistentId, null));
    }

    @Test
    @Transactional
    void getPixKeyById_ShouldThrowInvalidFilterCombinationException_WhenFiltersProvided() {
        ResponseEntity<PixKeyResponse> createResponse = pixKeyController.createPixKey(VALID_REQUEST);
        UUID id = UUID.fromString(createResponse.getBody().id());

        assertThrows(
                br.com.paulomoreira.pixkey.domain.exceptions.InvalidFilterCombinationException.class,
                () -> pixKeyController.getPixKeyById(id, "keyType=CPF")
        );
    }
}