package com.example.transactions.service.serviceImpl;

import com.example.transactions.exceptions.ApiException;
import com.example.transactions.models.entities.Account;
import com.example.transactions.models.entities.Transaction;
import com.example.transactions.models.enums.OperationType;
import com.example.transactions.models.requests.CreateTransactionRequest;
import com.example.transactions.models.response.TransactionResponse;
import com.example.transactions.repository.TransactionRepository;
import com.example.transactions.service.AccountService;
import com.example.transactions.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Implementation of the {@link TransactionService}.
 * Handles the core business logic, validation, and transaction boundaries
 * for recording financial operations against user accounts.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository repository;
    private final AccountService accountService;

    /**
     * Processes and records a new financial transaction.
     * Enforces business rules including amount validation (must not be zero),
     * operation type resolution, and amount normalization based on the operation
     * (e.g., automatically converting purchases to negative values).
     *
     * @param request the {@link CreateTransactionRequest} payload containing transaction details
     * @return a {@link TransactionResponse} representing the persisted transaction record
     * @throws ApiException with a 400 status if the amount is zero or the operation type is invalid
     * @throws ApiException with a 404 status if the referenced account does not exist
     */
    @Override
    @Transactional
    public TransactionResponse create(CreateTransactionRequest request) {
        validateAmount(request.getAmount());
        OperationType type = resolveType(request.getOperationTypeId());

        // Resolves the account or throws a 404 via the AccountService
        Account account = accountService.getEntity(request.getAccountId());

        Transaction saved = repository.save(Transaction.builder()
                .account(account)
                .operationType(type)
                .amount(type.normalize(request.getAmount()))
                .eventDate(OffsetDateTime.now())
                .build());

        log.info("Successfully recorded transaction id={} for accountId={} with type={}",
                saved.getTransactionId(), account.getAccountId(), type);

        return TransactionResponse.from(saved);
    }

    /**
     * Validates that the provided transaction amount is strictly non-zero.
     *
     * @param amount the {@link BigDecimal} amount to validate
     * @throws ApiException with a 400 status if the amount is exactly zero
     */
    private void validateAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            throw new ApiException(400, "amount must not be zero");
        }
    }

    /**
     * Resolves the corresponding {@link OperationType} enum from its numeric ID.
     *
     * @param id the numeric identifier of the operation type provided in the request
     * @return the resolved {@link OperationType}
     * @throws ApiException with a 400 status if the ID does not map to a recognized operation type
     */
    private OperationType resolveType(int id) {
        try {
            return OperationType.fromId(id);
        } catch (IllegalArgumentException e) {
            throw new ApiException(400, e.getMessage());
        }
    }
}