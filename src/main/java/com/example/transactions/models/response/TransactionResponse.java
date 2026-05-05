package com.example.transactions.models.response;

import com.example.transactions.models.entities.Transaction;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Data Transfer Object (DTO) representing a financial transaction response.
 * Utilizes Java Records for guaranteed immutability and efficient serialization.
 */
@Builder
public record TransactionResponse(

        @JsonProperty("transaction_id") Long transactionId,
        @JsonProperty("account_id") Long accountId,
        @JsonProperty("operation_type_id") int operationTypeId,
        @JsonProperty("amount") BigDecimal amount,
        @JsonProperty("event_date") OffsetDateTime eventDate
) {

    /**
     * Factory method to safely map a raw JPA {@link Transaction} entity into a
     * client-facing DTO.
     *
     * @param transaction the {@link Transaction} entity retrieved from the database
     * @return the mapped {@link TransactionResponse} payload, or null if the input is null
     */
    public static TransactionResponse from(Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        return TransactionResponse.builder()
                .transactionId(transaction.getTransactionId())
                .accountId(transaction.getAccount().getAccountId())
                .operationTypeId(transaction.getOperationType().getId())
                .amount(transaction.getAmount())
                .eventDate(transaction.getEventDate())
                .build();
    }
}