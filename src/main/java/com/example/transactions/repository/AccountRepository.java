package com.example.transactions.repository;

import com.example.transactions.models.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** Spring Data repository for {@link Account}. */
public interface AccountRepository extends JpaRepository<Account, Long> {

    /** Find an account by its (unique) document number, if any. */
    Optional<Account> findByDocumentNumber(String documentNumber);
}
