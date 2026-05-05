package com.example.transactions.controllers;

import com.example.transactions.models.requests.CreateTransactionRequest;
import com.example.transactions.models.response.TransactionResponse;
import com.example.transactions.service.TransactionService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionService service;

    @InjectMocks
    private TransactionController controller;

    @Test
    void create_ShouldReturn201WithBody() {
        CreateTransactionRequest request = CreateTransactionRequest.builder()
                .accountId(1L)
                .operationTypeId(4)
                .amount(new BigDecimal("10.00"))
                .build();
        TransactionResponse expected = TransactionResponse.builder()
                .transactionId(99L)
                .accountId(1L)
                .operationTypeId(4)
                .amount(new BigDecimal("10.00"))
                .eventDate(OffsetDateTime.now())
                .build();
        when(service.create(request)).thenReturn(expected);

        Response response = controller.create(request);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertSame(expected, response.getEntity());
    }
}
