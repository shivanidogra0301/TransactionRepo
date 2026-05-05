package com.example.transactions.integration.repository;

import com.example.transactions.models.entities.Account;
import com.example.transactions.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AccountRepositoryIT {

    @Autowired private AccountRepository repository;

    @Test
    void shouldFindByDocumentNumber() {
        Account account = Account.builder().documentNumber("TEST-123").build();
        repository.save(account);

        var found = repository.findByDocumentNumber("TEST-123");
        assertTrue(found.isPresent());
        assertEquals("TEST-123", found.get().getDocumentNumber());
    }
}