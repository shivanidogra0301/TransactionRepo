package com.example.transactions.models.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Request payload for creating a new account.
 * Mapped from incoming JSON requests in the REST controller.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequest {

    /**
     * The unique document identifier for the account owner (e.g., National ID, SSN).
     * Must not be null, empty, or consist entirely of whitespace.
     */
    @NotBlank
    @JsonProperty("document_number")
    private String documentNumber;
}