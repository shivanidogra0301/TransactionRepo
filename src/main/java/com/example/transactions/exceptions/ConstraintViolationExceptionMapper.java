package com.example.transactions.exceptions;

import com.example.transactions.models.response.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

/**
 * JAX-RS picks the most specific {@link ExceptionMapper}, so Jersey's built-in
 * {@code ExceptionMapper<ValidationException>} would otherwise win for
 * constraint violations and return an HTML error page. This mapper is more
 * specific and produces the same {@link ErrorResponse} shape used elsewhere.
 */
@Slf4j
@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
    private static final Logger logger = LoggerFactory.getLogger(ConstraintViolationExceptionMapper.class);

    @Override
    public Response toResponse(ConstraintViolationException ex) {
        String detail = ex.getConstraintViolations().stream()
                .map(v -> lastNode(v.getPropertyPath().toString()) + " " + v.getMessage())
                .collect(Collectors.joining("; "));
        logger.warn("Validation failed: {}", detail);
        int code = 400;
        return Response.status(code)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ErrorResponse(code, detail, ApiException.titleFor(code)))
                .build();
    }

    private static String lastNode(String path) {
        int idx = path.lastIndexOf('.');
        return idx >= 0 ? path.substring(idx + 1) : path;
    }
}
