package com.example.transactions.service.serviceImpl;

import com.example.transactions.exceptions.ApiException;
import com.example.transactions.models.entities.Account;
import com.example.transactions.models.requests.CreateAccountRequest;
import com.example.transactions.models.response.AccountResponse;
import com.example.transactions.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository repository;

    @InjectMocks
    private AccountServiceImpl service;

    private static CreateAccountRequest req(String document) {
        CreateAccountRequest r = new CreateAccountRequest();
        r.setDocumentNumber(document);
        return r;
    }

    @Test
    void createPersistsWhenDocumentIsNew() {
        when(repository.findByDocumentNumber("123")).thenReturn(Optional.empty());
        when(repository.save(any(Account.class)))
                .thenAnswer(inv -> {
                    Account a = inv.getArgument(0);
                    a.setAccountId(7L);
                    return a;
                });

        AccountResponse res = service.create(req("123"));

        assertEquals(7L, res.accountId());
        assertEquals("123", res.documentNumber());
    }

    @Test
    void createRejectsDuplicateDocument() {
        when(repository.findByDocumentNumber("dup"))
                .thenReturn(Optional.of(Account.builder().accountId(1L).documentNumber("dup").build()));

        ApiException ex = assertThrows(ApiException.class, () -> service.create(req("dup")));
        assertEquals(400, ex.getCode());
    }

    @Test
    void findByIdReturnsResponse() {
        when(repository.findById(5L))
                .thenReturn(Optional.of(Account.builder().accountId(5L).documentNumber("x").build()));

        assertEquals("x", service.findById(5L).documentNumber());
    }

    @ParameterizedTest(name = "{0} throws 404 when account is missing")
    @EnumSource(Lookup.class)
    void lookupsThrowApiException404WhenMissing(Lookup lookup) {
        when(repository.findById(9L)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class, () -> lookup.invoke(service, 9L));
        assertEquals(404, ex.getCode());
    }

    enum Lookup {
        FIND_BY_ID {
            @Override
            void invoke(AccountServiceImpl service, long id) {
                service.findById(id);
            }
        },
        GET_ENTITY {
            @Override
            void invoke(AccountServiceImpl service, long id) {
                service.getEntity(id);
            }
        };

        abstract void invoke(AccountServiceImpl service, long id);
    }
}
