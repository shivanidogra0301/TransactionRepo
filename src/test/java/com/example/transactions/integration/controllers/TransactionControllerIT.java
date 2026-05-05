package com.example.transactions.integration.controllers;

import com.example.transactions.integration.BaseIntegrationTest;
import com.example.transactions.models.requests.CreateAccountRequest;
import com.example.transactions.models.requests.CreateTransactionRequest;
import com.example.transactions.models.response.AccountResponse;
import com.example.transactions.models.response.ErrorResponse;
import com.example.transactions.models.response.TransactionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.function.LongFunction;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the Transaction Controller.
 * Verifies the end-to-end flow from the REST endpoint to the database.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TransactionControllerIT extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private Long validAccountId;

    /**
     * Set up a fresh account before each test to ensure a valid target for transactions.
     */
    @BeforeEach
    void setUp() {
        CreateAccountRequest accountRequest = new CreateAccountRequest("DOC-" + System.currentTimeMillis());
        ResponseEntity<AccountResponse> response = restTemplate.postForEntity("/api/accounts", accountRequest, AccountResponse.class);
        assertNotNull(response.getBody());
        this.validAccountId = response.getBody().accountId();
    }

    @Test
    void createTransaction_ShouldReturn200_AndNormalizeAmount() {
        // Arrange: Type 3 is Withdrawal, which should result in a negative amount
        CreateTransactionRequest request = CreateTransactionRequest.builder()
                .accountId(validAccountId)
                .operationTypeId(3)
                .amount(new BigDecimal("250.50"))
                .build();

        // Act
        ResponseEntity<TransactionResponse> response = restTemplate.postForEntity("/api/transactions", request, TransactionResponse.class);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(validAccountId, response.getBody().accountId());
        // Verify business logic: 250.50 Withdrawal must be -250.50
        assertEquals(0, new BigDecimal("-250.50").compareTo(response.getBody().amount()));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidTransactionScenarios")
    void createTransaction_ShouldReturnExpectedError(String scenario,
                                                     LongFunction<CreateTransactionRequest> requestFactory,
                                                     HttpStatus expectedStatus,
                                                     String expectedDetailFragment) {
        CreateTransactionRequest request = requestFactory.apply(validAccountId);

        ResponseEntity<ErrorResponse> response =
                restTemplate.postForEntity("/api/transactions", request, ErrorResponse.class);

        assertEquals(expectedStatus, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().detail().contains(expectedDetailFragment),
                () -> "expected detail to contain '" + expectedDetailFragment + "' but was '"
                        + response.getBody().detail() + "'");
    }

    private static Stream<Arguments> invalidTransactionScenarios() {
        LongFunction<CreateTransactionRequest> zeroAmount = accountId -> CreateTransactionRequest.builder()
                .accountId(accountId)
                .operationTypeId(1)
                .amount(BigDecimal.ZERO)
                .build();
        LongFunction<CreateTransactionRequest> unknownAccount = accountId -> CreateTransactionRequest.builder()
                .accountId(999_999L)
                .operationTypeId(1)
                .amount(new BigDecimal("10.00"))
                .build();
        LongFunction<CreateTransactionRequest> unknownOperation = accountId -> CreateTransactionRequest.builder()
                .accountId(accountId)
                .operationTypeId(99)
                .amount(new BigDecimal("10.00"))
                .build();

        return Stream.of(
                Arguments.of("400 when amount is zero", zeroAmount, HttpStatus.BAD_REQUEST, "amount must not be zero"),
                Arguments.of("404 when account not found", unknownAccount, HttpStatus.NOT_FOUND, "Account not found"),
                Arguments.of("400 when operation type is invalid", unknownOperation, HttpStatus.BAD_REQUEST, "Unknown operation_type_id")
        );
    }
}