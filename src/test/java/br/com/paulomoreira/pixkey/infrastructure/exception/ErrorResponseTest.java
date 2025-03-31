package br.com.paulomoreira.pixkey.infrastructure.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void shouldCreateErrorResponseWithCorrectValues() {
        int expectedStatus = 400;
        String expectedError = "Bad Request";
        String expectedMessage = "Invalid input parameter";

        ErrorResponse response = new ErrorResponse(expectedStatus, expectedError, expectedMessage);

        assertAll(
                () -> assertEquals(expectedStatus, response.status()),
                () -> assertEquals(expectedError, response.error()),
                () -> assertEquals(expectedMessage, response.message())
        );
    }


    @Test
    void shouldImplementEqualsAndHashCode() {
        ErrorResponse response1 = new ErrorResponse(500, "Internal Error", "Server error");
        ErrorResponse response2 = new ErrorResponse(500, "Internal Error", "Server error");
        ErrorResponse response3 = new ErrorResponse(400, "Bad Request", "Invalid input");

        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1, response3);
    }

    @Test
    void shouldImplementToString() {
        ErrorResponse response = new ErrorResponse(403, "Forbidden", "Access denied");

        String toStringResult = response.toString();

        assertAll(
                () -> assertTrue(toStringResult.contains("403")),
                () -> assertTrue(toStringResult.contains("Forbidden")),
                () -> assertTrue(toStringResult.contains("Access denied"))
        );
    }
}