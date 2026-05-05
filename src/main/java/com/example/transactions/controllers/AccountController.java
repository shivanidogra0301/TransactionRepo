package com.example.transactions.controllers;

import com.example.transactions.models.requests.CreateAccountRequest;
import com.example.transactions.models.response.AccountResponse;
import com.example.transactions.service.AccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * JAX-RS REST controller for managing account-related operations.
 * Provides endpoints for provisioning new accounts and retrieving existing account details.
 */
@Slf4j
@Component
@Path("/accounts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class AccountController {

    private final AccountService service;

    /**
     * Creates a new account based on the provided request payload.
     *
     * @param request the {@link CreateAccountRequest} containing the necessary data
     *                to provision a new account. Must pass bean validation constraints.
     * @return a {@link Response} containing the newly created {@link AccountResponse}
     *         entity and an HTTP 201 (Created) status code.
     */
    @POST
    public Response create(@Valid CreateAccountRequest request) {
        AccountResponse body = service.create(request);
        log.info("Successfully provisioned new account with ID: {}", body.accountId());
        return Response.status(Response.Status.CREATED).entity(body).build();
    }

    /**
     * Retrieves an existing account by its unique primary key identifier.
     *
     * @param accountId the unique ID of the account to fetch. Must be a strictly
     *                  positive number.
     * @return the {@link AccountResponse} representing the requested account details.
     *         (Note: A 404 Not Found response is expected if the account does not exist,
     *         typically handled by an ExceptionMapper down the line).
     */
    @GET
    @Path("/{accountId}")
    public AccountResponse get(@PathParam("accountId") @Positive Long accountId) {
        log.debug("Fetching account details for ID: {}", accountId);
        return service.findById(accountId);
    }
}