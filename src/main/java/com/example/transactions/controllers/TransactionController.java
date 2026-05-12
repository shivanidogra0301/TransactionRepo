package com.example.transactions.controllers;

import com.example.transactions.models.requests.CreateTransactionRequest;
import com.example.transactions.models.response.TransactionResponse;
import com.example.transactions.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Transactions", description = "Operations related to customer transactions")
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
    @Operation(
            summary = "Create a new Transaction",
            description = "Processes a financial transaction (debit or credit) and immediately updates the associated account balance."
    )
    @ApiResponse(
            responseCode = "201",
            description = "Transaction successfully created and recorded",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = TransactionResponse.class))
    )
    @ApiResponse(responseCode = "400", description = "Invalid payload (e.g., missing fields, zero amount, unknown operation type)")
    @ApiResponse(responseCode = "404", description = "The target Account ID does not exist")
    @ApiResponse(responseCode = "409", description = "Business rule violation (e.g., insufficient funds)")
    public Response create(@Valid CreateTransactionRequest request) {
        TransactionResponse body = service.create(request);
        log.info("Successfully recorded new transaction with ID: {}", body.transactionId());
        return Response.status(Response.Status.CREATED).entity(body).build();
    }
}