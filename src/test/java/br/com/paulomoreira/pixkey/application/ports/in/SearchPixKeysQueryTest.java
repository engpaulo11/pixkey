package br.com.paulomoreira.pixkey.application.ports.in;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

class SearchPixKeysQueryTest {

    @Test
    void shouldCreateSearchPixKeysQueryWithCorrectValues() {
        String keyType = "CPF";
        Integer branchNumber = 1234;
        Integer accountNumber = 567890;
        String accountHolderName = "João Silva";
        LocalDateTime createdAt = LocalDateTime.of(2024, 3, 30, 12, 0);
        LocalDateTime deactivatedAt = LocalDateTime.of(2024, 3, 31, 12, 0);

        SearchPixKeysQuery query = new SearchPixKeysQuery(
                keyType, branchNumber, accountNumber, accountHolderName, createdAt, deactivatedAt
        );

        assertThat(query.keyType()).isEqualTo(keyType);
        assertThat(query.branchNumber()).isEqualTo(branchNumber);
        assertThat(query.accountNumber()).isEqualTo(accountNumber);
        assertThat(query.accountHolderName()).isEqualTo(accountHolderName);
        assertThat(query.createdAt()).isEqualTo(createdAt);
        assertThat(query.deactivatedAt()).isEqualTo(deactivatedAt);
    }
}
