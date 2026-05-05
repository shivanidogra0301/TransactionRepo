package com.example.transactions.service.serviceImpl;

import com.example.transactions.exceptions.ApiException;
import com.example.transactions.models.entities.Account;
import com.example.transactions.models.entities.Transaction;
import com.example.transactions.models.requests.CreateTransactionRequest;
import com.example.transactions.models.response.TransactionResponse;
import com.example.transactions.repository.TransactionRepository;
import com.example.transactions.service.AccountService;
import com.example.transactions.service.serviceImpl.TransactionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository repository;
    @Mock
    private AccountService accountService;

    @InjectMocks
    private TransactionServiceImpl service;

    private static CreateTransactionRequest req(int opTypeId, BigDecimal amount) {
        CreateTransactionRequest r = new CreateTransactionRequest();
        r.setAccountId(1L);
        r.setOperationTypeId(opTypeId);
        r.setAmount(amount);
        return r;
    }

    private static Account account() {
        return Account.builder().accountId(1L).documentNumber("doc").build();
    }

    @ParameterizedTest(name = "opTypeId={0} amount={1} -> stored={2}")
    @CsvSource({
            "1,  50.00, -50.00",
            "2,  20.00, -20.00",
            "3,  75.00, -75.00",
            "4, 100.00, 100.00",
            "4,-100.00, 100.00"
    })
    void normalisesAmountByOperationType(int opTypeId, BigDecimal amount, BigDecimal expected) {
        when(accountService.getEntity(1L)).thenReturn(account());
        when(repository.save(any(Transaction.class))).thenAnswer(inv -> {
            Transaction t = inv.getArgument(0);
            t.setTransactionId(99L);
            t.setEventDate(OffsetDateTime.now());
            return t;
        });

        TransactionResponse res = service.create(req(opTypeId, amount));
        assertEquals(0, expected.compareTo(res.amount()));
    }

    @Test
    void rejectsZeroAmount() {
        ApiException ex = assertThrows(ApiException.class,
                () -> service.create(req(1, BigDecimal.ZERO)));
        assertEquals(400, ex.getCode());
    }

    @Test
    void rejectsUnknownOperationType() {
        ApiException ex = assertThrows(ApiException.class,
                () -> service.create(req(99, new BigDecimal("10"))));
        assertEquals(400, ex.getCode());
    }

    @Test
    void propagatesAccountNotFound() {
        when(accountService.getEntity(1L))
                .thenThrow(new ApiException(404, "nope"));
        ApiException ex = assertThrows(ApiException.class,
                () -> service.create(req(1, new BigDecimal("10"))));
        assertEquals(404, ex.getCode());
    }
}
