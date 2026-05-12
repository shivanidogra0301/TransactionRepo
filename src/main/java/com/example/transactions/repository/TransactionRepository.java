package com.example.transactions.repository;

import com.example.transactions.models.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/** Spring Data repository for {@link Transaction}. */
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("Select t from Transaction t where t.operationType != 'CREDIT_VOUCHER' and t.balance < 0 and t.account.accountId= :accountId")
    List<Transaction> getTransactionByOperationTypeAndAmount(@Param("accountId") Long accountId);
}
