package com.example.transactions.exceptions;

import com.example.transactions.models.response.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionMapperTest {

    private final GlobalExceptionMapper mapper = new GlobalExceptionMapper();

    @ParameterizedTest(name = "ApiException({0}, {1}) -> {0}")
    @CsvSource({
            "400, 'Bad Request Test'",
            "404, 'Not Found Test'",
            "409, 'Conflict Test'"
    })
    void toResponse_ShouldHandleApiException(int code, String message) {
        Response response = mapper.toResponse(new ApiException(code, message));

        assertEquals(code, response.getStatus());
        ErrorResponse entity = (ErrorResponse) response.getEntity();
        assertEquals(message, entity.detail());
        assertEquals(code, entity.code());
        assertEquals(ApiException.titleFor(code), entity.title());
    }

    @ParameterizedTest(name = "{0} -> {1}")
    @MethodSource("webApplicationExceptions")
    void toResponse_ShouldHandleWebApplicationException(WebApplicationException ex, int expectedStatus) {
        Response response = mapper.toResponse(ex);
        assertEquals(expectedStatus, response.getStatus());
    }

    private static Stream<Arguments> webApplicationExceptions() {
        return Stream.of(
                Arguments.of(new NotFoundException("missing"), 404),
                Arguments.of(new NotAuthorizedException("nope", "Basic"), 401),
                Arguments.of(new WebApplicationException("teapot", 418), 418)
        );
    }

    @Test
    void toResponse_ShouldHandleConstraintViolationExceptionWithLeafFieldNames() {
        ConstraintViolation<?> v = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("create.request.amount");
        when(v.getPropertyPath()).thenReturn(path);
        when(v.getMessage()).thenReturn("must not be zero");
        Response response = mapper.toResponse(new ConstraintViolationException(Set.of(v)));

        assertEquals(400, response.getStatus());
        ErrorResponse entity = (ErrorResponse) response.getEntity();
        assertTrue(entity.detail().contains("amount must not be zero"), entity.detail());
    }

    @Test
    void toResponse_ShouldHandleGenericThrowable() {
        Response response = mapper.toResponse(new RuntimeException("Crash"));

        assertEquals(500, response.getStatus());
        ErrorResponse entity = (ErrorResponse) response.getEntity();
        assertEquals("An unexpected error occurred on the server.", entity.detail());
        assertEquals("INTERNAL_SERVER_ERROR", entity.title());
    }
}