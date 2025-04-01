package br.com.paulomoreira.pixkey.infrastructure.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PixKeyEntityTest {

    private PixKeyEntity pixKeyEntity;
    private static final UUID ID = UUID.randomUUID();
    private static final String KEY_VALUE = "12345678901";
    private static final String TYPE = "CPF";
    private static final String ACCOUNT_TYPE = "CORRENTE";
    private static final Integer BRANCH_NUMBER = 1234;
    private static final Integer ACCOUNT_NUMBER = 98765432;
    private static final String ACCOUNT_HOLDER_NAME = "João";
    private static final String ACCOUNT_HOLDER_LAST_NAME = "Silva";
    private static final LocalDateTime CREATED_AT = LocalDateTime.now();
    private static final boolean IS_LEGAL_PERSON = false;

    @BeforeEach
    void setUp() {
        pixKeyEntity = new PixKeyEntity(
                ID, KEY_VALUE, TYPE, ACCOUNT_TYPE, BRANCH_NUMBER, ACCOUNT_NUMBER,
                ACCOUNT_HOLDER_NAME, ACCOUNT_HOLDER_LAST_NAME, CREATED_AT, true, null
        );
    }

    @Test
    void testDefaultConstructor() {
        PixKeyEntity entity = new PixKeyEntity();
        assertNull(entity.getId());
        assertNull(entity.getKeyValue());
        assertNull(entity.getType());
        assertNull(entity.getAccountType());
        assertNull(entity.getBranchNumber());
        assertNull(entity.getAccountNumber());
        assertNull(entity.getAccountHolderName());
        assertNull(entity.getAccountHolderLastName());
        assertNull(entity.getCreatedAt());
        assertTrue(entity.isActive()); // Default value is true
        assertNull(entity.getDeactivatedAt());
        assertFalse(entity.isLegalPerson()); // Default value for primitive boolean is false
    }

    @Test
    void testParameterizedConstructor() {
        assertEquals(ID, pixKeyEntity.getId());
        assertEquals(KEY_VALUE, pixKeyEntity.getKeyValue());
        assertEquals(TYPE, pixKeyEntity.getType());
        assertEquals(ACCOUNT_TYPE, pixKeyEntity.getAccountType());
        assertEquals(BRANCH_NUMBER, pixKeyEntity.getBranchNumber());
        assertEquals(ACCOUNT_NUMBER, pixKeyEntity.getAccountNumber());
        assertEquals(ACCOUNT_HOLDER_NAME, pixKeyEntity.getAccountHolderName());
        assertEquals(ACCOUNT_HOLDER_LAST_NAME, pixKeyEntity.getAccountHolderLastName());
        assertEquals(CREATED_AT, pixKeyEntity.getCreatedAt());
        assertTrue(pixKeyEntity.isActive());
        assertNull(pixKeyEntity.getDeactivatedAt());
        assertFalse(pixKeyEntity.isLegalPerson()); // Verifica o valor inicial
    }

    @Test
    void testSettersAndGetters() {
        PixKeyEntity entity = new PixKeyEntity();
        entity.setId(ID);
        entity.setKeyValue(KEY_VALUE);
        entity.setType(TYPE);
        entity.setAccountType(ACCOUNT_TYPE);
        entity.setBranchNumber(BRANCH_NUMBER);
        entity.setAccountNumber(ACCOUNT_NUMBER);
        entity.setAccountHolderName(ACCOUNT_HOLDER_NAME);
        entity.setAccountHolderLastName(ACCOUNT_HOLDER_LAST_NAME);
        entity.setCreatedAt(CREATED_AT);
        entity.setActive(false);
        entity.setDeactivatedAt(CREATED_AT.plusDays(1));
        entity.setLegalPerson(true);

        assertEquals(ID, entity.getId());
        assertEquals(KEY_VALUE, entity.getKeyValue());
        assertEquals(TYPE, entity.getType());
        assertEquals(ACCOUNT_TYPE, entity.getAccountType());
        assertEquals(BRANCH_NUMBER, entity.getBranchNumber());
        assertEquals(ACCOUNT_NUMBER, entity.getAccountNumber());
        assertEquals(ACCOUNT_HOLDER_NAME, entity.getAccountHolderName());
        assertEquals(ACCOUNT_HOLDER_LAST_NAME, entity.getAccountHolderLastName());
        assertEquals(CREATED_AT, entity.getCreatedAt());
        assertFalse(entity.isActive());
        assertEquals(CREATED_AT.plusDays(1), entity.getDeactivatedAt());
        assertTrue(entity.isLegalPerson());
    }

    @Test
    void testIsActive() {
        assertTrue(pixKeyEntity.isActive()); // Inicialmente true
        pixKeyEntity.setActive(false);
        assertFalse(pixKeyEntity.isActive());
    }

    @Test
    void testIsLegalPersonInitialization() {
        // O construtor atual não seta isLegalPerson explicitamente, então será false por padrão
        assertFalse(pixKeyEntity.isLegalPerson());

        // Testa com um valor explícito
        PixKeyEntity entityWithLegalPerson = new PixKeyEntity();
        entityWithLegalPerson.setLegalPerson(true);
        assertTrue(entityWithLegalPerson.isLegalPerson());
    }

    @Test
    void testConstructorWithNullValues() {
        PixKeyEntity entity = new PixKeyEntity(
                null, null, null, null, null, null, null, null, null, null, null
        );
        assertNull(entity.getId());
        assertNull(entity.getKeyValue());
        assertNull(entity.getType());
        assertNull(entity.getAccountType());
        assertNull(entity.getBranchNumber());
        assertNull(entity.getAccountNumber());
        assertNull(entity.getAccountHolderName());
        assertNull(entity.getAccountHolderLastName());
        assertNull(entity.getCreatedAt());
        assertNull(entity.getActive()); // Pode ser null porque é Boolean, não boolean
        assertNull(entity.getDeactivatedAt());
        assertFalse(entity.isLegalPerson()); // Default para boolean primitivo
    }
}
