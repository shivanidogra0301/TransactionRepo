package com.example.transactions.service;

import com.example.transactions.models.entities.Account;
import com.example.transactions.models.requests.CreateAccountRequest;
import com.example.transactions.models.response.AccountResponse;

/**
 * Defines the contract for account-related business operations.
 * Provides the core use-cases for provisioning new accounts and securely
 * retrieving existing account data across different architectural layers.
 */
public interface AccountService {

    /**
     * Provisions a new account based on the provided request data.
     *
     * @param request the {@link CreateAccountRequest} containing the necessary data to create an account
     * @return an {@link AccountResponse} representing the newly created account
     */
    AccountResponse create(CreateAccountRequest request);

    /**
     * Retrieves the data transfer object (DTO) representation of an account by its unique identifier.
     * Designed to be exposed safely to external clients via the presentation layer.
     *
     * @param accountId the unique primary key of the account to fetch
     * @return the {@link AccountResponse} containing the account details
     */
    AccountResponse findById(Long accountId);

    /**
     * Retrieves the raw JPA {@link Account} entity by its unique identifier.
     * This method is strictly intended for internal use by other business services
     * that require direct entity access (e.g., to establish foreign key relationships).
     *
     * @param accountId the unique primary key of the account to retrieve
     * @return the resolved {@link Account} database entity
     */
    Account getEntity(Long accountId);
}