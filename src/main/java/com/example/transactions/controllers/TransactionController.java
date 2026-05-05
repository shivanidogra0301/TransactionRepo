package com.example.transactions.controllers;

import com.example.transactions.models.requests.CreateTransactionRequest;
import com.example.transactions.models.response.TransactionResponse;
import com.example.transactions.service.TransactionService;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * JAX-RS REST controller for managing financial transactions.
 * Provides endpoints for recording and processing transactions against accounts.
 */
@Slf4j
@Component
@Path("/transactions")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService service;

    /**
     * Records a new financial transaction based on the provided request payload.
     *
     * @param request the {@link CreateTransactionRequest} containing the transaction details
     *                (e.g., amount, account identifiers, transaction type).
     *                Must pass bean validation constraints.
     * @return a {@link Response} containing the resulting {@link TransactionResponse}
     *         entity and an HTTP 201 (Created) status code upon successful execution.
     */
    @POST
    public Response create(@Valid CreateTransactionRequest request) {
        TransactionResponse body = service.create(request);
        log.info("Successfully recorded new transaction with ID: {}", body.transactionId());
        return Response.status(Response.Status.CREATED).entity(body).build();
    }
}