package com.example.transactions.models.response;

import com.example.transactions.models.entities.Account;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

/**
 * Data Transfer Object (DTO) representing a customer account response.
 */
@Builder
public record AccountResponse(

        @JsonProperty("account_id") Long accountId,
        @JsonProperty("document_number") String documentNumber
) {

    /**
     * Factory method to safely map a raw JPA database entity into a client-facing DTO.
     *
     * @param account the {@link Account} entity retrieved from the database
     * @return the mapped {@link AccountResponse} payload, or null if the input is null
     */
    public static AccountResponse from(Account account) {
        if (account == null) {
            return null;
        }

        return AccountResponse.builder()
                .accountId(account.getAccountId())
                .documentNumber(account.getDocumentNumber())
                .build();
    }
}