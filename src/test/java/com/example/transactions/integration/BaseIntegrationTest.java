package com.example.transactions.integration;

import com.example.transactions.repository.AccountRepository;
import com.example.transactions.repository.TransactionRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;

/**
 * Base class for all integration tests.
 * Provides a shared Spring context, a pre-configured REST client,
 * and automatic database cleanup between test runs.
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseIntegrationTest {

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected AccountRepository accountRepository;

    @Autowired
    protected TransactionRepository transactionRepository;

    /**
     * Ensures each test starts with a clean database.
     * Transaction records must be deleted before accounts due to Foreign Key constraints.
     */
    @AfterEach
    void tearDown() {
        transactionRepository.deleteAllInBatch();
        accountRepository.deleteAllInBatch();
    }
}