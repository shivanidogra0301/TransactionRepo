package com.example.transactions.exceptions;

import com.example.transactions.models.response.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConstraintViolationExceptionMapperTest {

    private final ConstraintViolationExceptionMapper mapper = new ConstraintViolationExceptionMapper();

    @Test
    void toResponse_ShouldMapNestedPathsToLeafField() {
        Set<ConstraintViolation<?>> violations = new LinkedHashSet<>();
        violations.add(violation("create.request.amount", "must not be zero"));
        violations.add(violation("documentNumber", "must not be blank"));

        Response response = mapper.toResponse(new ConstraintViolationException(violations));

        assertEquals(400, response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
        ErrorResponse body = (ErrorResponse) response.getEntity();
        assertTrue(body.detail().contains("amount must not be zero"), body.detail());
        assertTrue(body.detail().contains("documentNumber must not be blank"), body.detail());
        assertEquals("BAD_REQUEST", body.title());
    }

    private static ConstraintViolation<?> violation(String path, String message) {
        ConstraintViolation<?> v = mock(ConstraintViolation.class);
        Path p = mock(Path.class);
        when(p.toString()).thenReturn(path);
        when(v.getPropertyPath()).thenReturn(p);
        when(v.getMessage()).thenReturn(message);
        return v;
    }
}
