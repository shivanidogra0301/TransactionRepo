package com.example.transactions.service;

import com.example.transactions.models.requests.CreateTransactionRequest;
import com.example.transactions.models.response.TransactionResponse;

/**
 * Defines the contract for financial transaction operations.
 * Provides the core use-cases for safely recording and processing
 * transactions against user accounts.
 */
public interface TransactionService {

    /**
     * Records a new financial transaction based on the provided request data.
     *
     * @param request the {@link CreateTransactionRequest} containing the transaction details
     *                (e.g., account identifier, operation type, and amount)
     * @return a {@link TransactionResponse} representing the newly recorded transaction
     */
    TransactionResponse create(CreateTransactionRequest request);
}