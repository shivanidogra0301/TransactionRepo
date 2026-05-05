package com.example.transactions.models.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Request payload for recording a new financial transaction.
 * Mapped from incoming JSON requests in the REST controller.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransactionRequest {

    /**
     * The unique primary key of the account this transaction belongs to.
     * Must be a strictly positive number.
     */
    @NotNull(message = "account_id is strictly required")
    @Positive(message = "account_id must be a positive number")
    @JsonProperty("account_id")
    private Long accountId;

    /**
     * The numeric identifier of the operation type (e.g., 1 for Purchase, 4 for Credit).
     * Must be a strictly positive number.
     */
    @NotNull(message = "operation_type_id is strictly required")
    @Positive(message = "operation_type_id must be a positive number")
    @JsonProperty("operation_type_id")
    private Integer operationTypeId;

    /**
     * The monetary value of the transaction.
     * The mathematical sign will be automatically normalized by the server based on the operation type.
     */
    @NotNull(message = "amount is strictly required")
    @JsonProperty("amount")
    private BigDecimal amount;
}