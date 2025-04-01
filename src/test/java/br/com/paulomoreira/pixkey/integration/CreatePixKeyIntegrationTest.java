package br.com.paulomoreira.pixkey.integration;

import br.com.paulomoreira.pixkey.adapters.in.dto.request.CreatePixKeyRequest;
import br.com.paulomoreira.pixkey.adapters.in.dto.response.PixKeyResponse;
import br.com.paulomoreira.pixkey.adapters.in.rest.PixKeyController;
import br.com.paulomoreira.pixkey.domain.exceptions.DuplicateKeyException;
import br.com.paulomoreira.pixkey.domain.exceptions.InvalidKeyException;
import br.com.paulomoreira.pixkey.domain.exceptions.KeyLimitExceededException;
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
class CreatePixKeyIntegrationTest {

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
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update"); // ou "validate" se usar Flyway
        registry.add("spring.jpa.properties.hibernate.jdbc.batch_size", () -> "20");
        registry.add("spring.jpa.properties.hibernate.order_inserts", () -> "true");
        registry.add("spring.jpa.properties.hibernate.order_updates", () -> "true");
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

    // Constantes para reuso nos testes
    private static final CreatePixKeyRequest VALID_CPF_REQUEST = new CreatePixKeyRequest(
            KeyType.CPF, "84315720003", AccountType.CORRENTE, 1234, 98765432, "João", "Silva"
    );
    private static final CreatePixKeyRequest VALID_CNPJ_REQUEST = new CreatePixKeyRequest(
            KeyType.CNPJ, "47960950000121", AccountType.CORRENTE, 1234, 98765432, "Empresa", "LTDA"
    );
    private static final CreatePixKeyRequest VALID_EMAIL_REQUEST = new CreatePixKeyRequest(
            KeyType.EMAIL, "joao.silva@example.com", AccountType.CORRENTE, 1234, 98765432, "João", "Silva"
    );
    private static final CreatePixKeyRequest VALID_PHONE_REQUEST = new CreatePixKeyRequest(
            KeyType.CELULAR, "+5511999999999", AccountType.CORRENTE, 1234, 98765432, "João", "Silva"
    );
    private static final CreatePixKeyRequest VALID_RANDOM_REQUEST = new CreatePixKeyRequest(
            KeyType.ALEATORIO, "123e4567-e89b-12d3-a456-426614174000", AccountType.CORRENTE, 1234, 98765432, "João", "Silva"
    );

    /*****************************************************************
     * CRITÉRIO 2: Geração de código único (UUID) e persistência básica
     *****************************************************************/
    @Test
    @Transactional
    void createPixKey_ShouldPersistAllRequiredFields() {
        ResponseEntity<PixKeyResponse> response = pixKeyController.createPixKey(VALID_CPF_REQUEST);
        UUID id = UUID.fromString(response.getBody().id());

        PixKeyEntity savedEntity = pixKeyJpaRepository.findById(id).orElseThrow();

        assertNotNull(savedEntity.getId());
        assertEquals(KeyType.CPF.toString(), savedEntity.getType());
        assertEquals("84315720003", savedEntity.getKeyValue());
        assertEquals(AccountType.CORRENTE.toString(), savedEntity.getAccountType());
        assertEquals(1234, savedEntity.getBranchNumber());
        assertEquals(98765432, savedEntity.getAccountNumber());
        assertEquals("João", savedEntity.getAccountHolderName());
        assertEquals("Silva", savedEntity.getAccountHolderLastName());
        assertNotNull(savedEntity.getCreatedAt());
        assertTrue(savedEntity.isActive());
        assertNull(savedEntity.getDeactivatedAt());
    }

    /*****************************************************************
     * CRITÉRIO 6: Validações de formato por tipo de chave
     *****************************************************************/

    // --- CPF (6c) ---
    @Test
    @Transactional
    void createPixKey_ShouldReturnOk_WhenValidCPFRequest() {
        ResponseEntity<PixKeyResponse> response = pixKeyController.createPixKey(VALID_CPF_REQUEST);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Transactional
    void createPixKey_ShouldThrowInvalidKeyException_WhenInvalidCPF() {
        var invalidRequest = new CreatePixKeyRequest(
                KeyType.CPF, "12345678901", AccountType.CORRENTE, 1234, 98765432, "João", "Silva"
        );
        InvalidKeyException exception = assertThrows(
                InvalidKeyException.class,
                () -> pixKeyController.createPixKey(invalidRequest)
        );
        assertTrue(exception.getMessage().contains("Invalid CPF"));
    }

    // --- CNPJ (6d) ---
    @Test
    @Transactional
    void createPixKey_ShouldReturnOk_WhenValidCNPJ() {
        ResponseEntity<PixKeyResponse> response = pixKeyController.createPixKey(VALID_CNPJ_REQUEST);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Transactional
    void createPixKey_ShouldThrowInvalidKeyException_WhenInvalidCNPJ() {
        var invalidRequest = new CreatePixKeyRequest(
                KeyType.CNPJ, "12345678901234", AccountType.CORRENTE, 1234, 98765432, "Empresa", "LTDA"
        );
        InvalidKeyException exception = assertThrows(
                InvalidKeyException.class,
                () -> pixKeyController.createPixKey(invalidRequest)
        );
        assertTrue(exception.getMessage().contains("Invalid CNPJ"));
    }

    // --- Email (6b) ---
    @Test
    @Transactional
    void createPixKey_ShouldReturnOk_WhenValidEmailRequest() {
        ResponseEntity<PixKeyResponse> response = pixKeyController.createPixKey(VALID_EMAIL_REQUEST);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Transactional
    void createPixKey_ShouldThrowInvalidKeyException_WhenInvalidEmailFormat() {
        var invalidRequest = new CreatePixKeyRequest(
                KeyType.EMAIL, "email-invalido@", AccountType.CORRENTE, 1234, 98765432, "João", "Silva"
        );
        InvalidKeyException exception = assertThrows(
                InvalidKeyException.class,
                () -> pixKeyController.createPixKey(invalidRequest)
        );
        assertTrue(exception.getMessage().contains("valid email format"));
    }

    @Test
    @Transactional
    void createPixKey_ShouldThrowInvalidKeyException_WhenEmailExceedsMaxLength() {
        var invalidRequest = new CreatePixKeyRequest(
                KeyType.EMAIL, "a".repeat(78) + "@example.com", AccountType.CORRENTE, 1234, 98765432, "João", "Silva"
        );
        InvalidKeyException exception = assertThrows(
                InvalidKeyException.class,
                () -> pixKeyController.createPixKey(invalidRequest)
        );
        assertTrue(exception.getMessage().contains("not exceed 77 characters"));
    }

    // --- Telefone (6a) ---
    @Test
    @Transactional
    void createPixKey_ShouldReturnOk_WhenValidPhoneRequest() {
        ResponseEntity<PixKeyResponse> response = pixKeyController.createPixKey(VALID_PHONE_REQUEST);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Transactional
    void createPixKey_ShouldThrowInvalidKeyException_WhenInvalidPhoneFormat() {
        var invalidRequest = new CreatePixKeyRequest(
                KeyType.CELULAR, "11999999999", AccountType.CORRENTE, 1234, 98765432, "João", "Silva"
        );
        InvalidKeyException exception = assertThrows(
                InvalidKeyException.class,
                () -> pixKeyController.createPixKey(invalidRequest)
        );
        assertTrue(exception.getMessage().contains("must start with '+'"));
    }

    // --- Chave Aleatória (6e) ---
    @Test
    @Transactional
    void createPixKey_ShouldReturnOk_WhenValidRandomKey() {
        ResponseEntity<PixKeyResponse> response = pixKeyController.createPixKey(VALID_RANDOM_REQUEST);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Transactional
    void createPixKey_ShouldThrowInvalidKeyException_WhenInvalidRandomKey() {
        var invalidRequest = new CreatePixKeyRequest(
                KeyType.ALEATORIO, "invalid-random-key", AccountType.CORRENTE, 1234, 98765432, "João", "Silva"
        );
        InvalidKeyException exception = assertThrows(
                InvalidKeyException.class,
                () -> pixKeyController.createPixKey(invalidRequest)
        );
        assertTrue(exception.getMessage().contains("valid UUID"));
    }


    /*****************************************************************
     * CRITÉRIO 1: Unicidade da chave
     *****************************************************************/
    @Test
    @Transactional
    void createPixKey_ShouldThrowDuplicateKeyException_WhenKeyAlreadyExists() {
        pixKeyController.createPixKey(VALID_CPF_REQUEST); // Primeira inserção
        DuplicateKeyException exception = assertThrows(
                DuplicateKeyException.class,
                () -> pixKeyController.createPixKey(VALID_CPF_REQUEST) // Segunda inserção
        );
        assertTrue(exception.getMessage().contains("Key already exists"));
    }

    /*****************************************************************
     * CRITÉRIO 3: Limite de chaves por accountNumber (5 para PF, 20 para PJ)
     *****************************************************************/
    @Test
    @Transactional
    void createPixKey_ShouldThrowKeyLimitExceededException_WhenExceedingLimitForCPF() {
        String[] cpfsValidos = {
                "52998224725", "55299173059", "18103807079", "16048025025", "69356544085",
                "98692850071" // CPF que excederá o limite
        };

        for (int i = 0; i < 5; i++) {
            var request = new CreatePixKeyRequest(
                    KeyType.CPF,
                    cpfsValidos[i],
                    AccountType.CORRENTE,
                    1234,
                    98765432,
                    "Cliente " + i,
                    "Sobrenome"
            );
            pixKeyController.createPixKey(request);
        }

        var excessRequest = new CreatePixKeyRequest(
                KeyType.CPF,
                cpfsValidos[5],
                AccountType.CORRENTE,
                1234,
                98765432,
                "Cliente Excedente",
                "Sobrenome"
        );

        assertThrows(
                KeyLimitExceededException.class,
                () -> pixKeyController.createPixKey(excessRequest)
        );
    }

    @Test
    @Transactional
    void createPixKey_ShouldAllow20KeysForCNPJ() {
        String[] cnpjsValidosUnicos = {
                "11444777000161", "11966962000116", "49449413000147",
                "29226588000188", "93031067000174", "80771618000194",
                "94551232000181", "23496499000175", "68070132000123",
                "00608349000100", "13578703000143", "73298413000178",
                "77812540000102", "02837121000118", "17969526000187",
                "88176751000104", "07291909000195", "11331414000110",
                "25360814000168",
        };

        for (String cnpj : cnpjsValidosUnicos) {
            var request = new CreatePixKeyRequest(
                    KeyType.CNPJ,
                    cnpj,
                    AccountType.CORRENTE,
                    1234,
                    98765432,
                    "Empresa " + cnpj.substring(0, 4),  // Nome único para cada CNPJ
                    "LTDA"
            );
            pixKeyController.createPixKey(request);
        }

        // Teste adicional para verificar o limite
        ResponseEntity<PixKeyResponse> response = pixKeyController.createPixKey(
                new CreatePixKeyRequest(
                        KeyType.CNPJ,
                        "26686902000117",
                        AccountType.CORRENTE,
                        1234,
                        98765432,
                        "Empresa Teste",
                        "LTDA"
                )
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }}