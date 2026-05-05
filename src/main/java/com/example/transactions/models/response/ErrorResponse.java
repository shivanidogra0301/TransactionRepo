package com.example.transactions.models.response;

import lombok.Builder;

/**
 * Standardized API error payload.
 * Provides a consistent structure for conveying failure details to clients,
 * adhering to established REST error reporting patterns.
 */
@Builder
public record ErrorResponse(

        /** The HTTP status code or internal application error code. */
        int code,

        /** A descriptive message providing context on why the error occurred. */
        String detail,

        /** A brief, human-readable summary of the error type. */
        String title
) {
}