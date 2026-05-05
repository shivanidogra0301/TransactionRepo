package com.example.transactions.exceptions;

import com.example.transactions.models.response.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.util.stream.Collectors;

/**
 * Global exception interceptor for the JAX-RS layer.
 * Standardizes all application errors into a uniform JSON {@link ErrorResponse}.
 */
@Slf4j
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable ex) {
        // 1. Handle custom business exceptions (e.g., 400 Already Exists, 404 Not Found)
        if (ex instanceof ApiException app) {
            log.warn("API business error [{}]: {}", app.getCode(), app.getMessage());
            return build(app.getCode(), app.getMessage());
        }

        // 2. Handle JSR-303 Validation errors (e.g., @Positive, @NotBlank)
        if (ex instanceof ConstraintViolationException cve) {
            String detail = cve.getConstraintViolations().stream()
                    .map(v -> lastNode(v.getPropertyPath().toString()) + " " + v.getMessage())
                    .collect(Collectors.joining("; "));
            log.warn("Validation constraint violation: {}", detail);
            return build(HttpStatus.BAD_REQUEST.value(), detail);
        }

        // 3. Handle standard JAX-RS web exceptions
        if (ex instanceof WebApplicationException wae) {
            int status = wae.getResponse().getStatus();
            log.warn("Web layer exception [{}]: {}", status, wae.getMessage());
            return build(status, wae.getMessage());
        }

        // 4. Handle unexpected system crashes (500 Internal Server Error)
        // CRITICAL: We pass the Throwable 'ex' as the second argument to log.error()
        // to ensure the full stack trace is printed in the logs for debugging.
        log.error("Unhandled system exception encountered", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred on the server.");
    }

    /**
     * Maps the error details to a standardized Response using the ErrorResponse Record Builder.
     */
    private static Response build(int code, String detail) {
        ErrorResponse entity = ErrorResponse.builder()
                .code(code)
                .detail(detail)
                .title(ApiException.titleFor(code))
                .build();

        return Response.status(code)
                .type(MediaType.APPLICATION_JSON)
                .entity(entity)
                .build();
    }

    /**
     * Extracts the leaf node of a validation path (e.g., "create.request.amount" -> "amount").
     */
    private static String lastNode(String path) {
        int idx = path.lastIndexOf('.');
        return idx >= 0 ? path.substring(idx + 1) : path;
    }
}