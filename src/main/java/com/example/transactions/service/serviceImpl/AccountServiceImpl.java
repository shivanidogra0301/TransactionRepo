package com.example.transactions.service.serviceImpl;

import com.example.transactions.exceptions.ApiException;
import com.example.transactions.models.entities.Account;
import com.example.transactions.models.requests.CreateAccountRequest;
import com.example.transactions.models.response.AccountResponse;
import com.example.transactions.repository.AccountRepository;
import com.example.transactions.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the {@link AccountService}.
 * Handles the core business logic and transaction boundaries for managing accounts.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository repository;

    /**
     * Provisions a new account in the system.
     * Enforces the business rule that no two accounts may share the same document number.
     *
     * @param request the {@link CreateAccountRequest} payload containing the document number
     * @return an {@link AccountResponse} representing the newly saved account
     * @throws ApiException with a 400 status if an account with the requested document number already exists
     */
    @Override
    @Transactional
    public AccountResponse create(CreateAccountRequest request) {
        repository.findByDocumentNumber(request.getDocumentNumber()).ifPresent(a -> {
            throw new ApiException(400, "Account with document_number already exists");
        });

        Account saved = repository.save(Account.builder()
                .documentNumber(request.getDocumentNumber())
                .build());

        log.info("Successfully created new account with id={}", saved.getAccountId());

        return AccountResponse.from(saved);
    }

    /**
     * Retrieves the data transfer object (DTO) representation of an account.
     *
     * @param accountId the unique primary key of the account to fetch
     * @return the {@link AccountResponse} containing the account details
     * @throws ApiException with a 404 status if the account does not exist
     */
    @Override
    @Transactional(readOnly = true)
    public AccountResponse findById(Long accountId) {
        return AccountResponse.from(getEntity(accountId));
    }

    /**
     * Internal business method to fetch the raw JPA {@link Account} entity.
     * Designed to be utilized by other services (e.g., TransactionService) that
     * need to establish foreign key relationships with an actual database entity.
     *
     * @param accountId the unique primary key of the account to retrieve
     * @return the resolved {@link Account} database entity
     * @throws ApiException with a 404 status if the account cannot be located
     */
    @Override
    @Transactional(readOnly = true)
    public Account getEntity(Long accountId) {
        return repository.findById(accountId)
                .orElseThrow(() -> new ApiException(404, "Account not found: " + accountId));
    }
}
