package com.example.transactions.repository;

import com.example.transactions.models.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data repository for {@link Transaction}. */
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
