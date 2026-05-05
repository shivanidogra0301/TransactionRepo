package com.example.transactions.exceptions;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApiExceptionTest {

    @ParameterizedTest(name = "code {0} -> title {1}")
    @CsvSource({
            "400, BAD_REQUEST",
            "404, NOT_FOUND",
            "500, INTERNAL_SERVER_ERROR",
            "999, ERROR"
    })
    void titleFor_ShouldMatchHttpStatusName(int code, String expectedTitle) {
        assertEquals(expectedTitle, ApiException.titleFor(code));
    }

    @ParameterizedTest(name = "code {0} message {1}")
    @CsvSource({
            "400, 'bad input'",
            "404, 'missing'",
            "500, 'boom'"
    })
    void apiException_ShouldExposeCodeMessageAndTitle(int code, String message) {
        ApiException ex = new ApiException(code, message);
        assertEquals(code, ex.getCode());
        assertEquals(message, ex.getMessage());
        assertEquals(ApiException.titleFor(code), ex.getTitle());
    }
}
