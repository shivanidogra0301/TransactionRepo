package com.example.transactions.controllers;

import com.example.transactions.models.requests.CreateAccountRequest;
import com.example.transactions.models.response.AccountResponse;
import com.example.transactions.service.AccountService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock
    private AccountService service;

    @InjectMocks
    private AccountController controller;

    @Test
    void create_ShouldReturn201WithBody() {
        CreateAccountRequest request = new CreateAccountRequest("doc-1");
        AccountResponse expected = AccountResponse.builder().accountId(1L).documentNumber("doc-1").build();
        when(service.create(request)).thenReturn(expected);

        Response response = controller.create(request);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertSame(expected, response.getEntity());
    }

    @Test
    void get_ShouldDelegateToService() {
        AccountResponse expected = AccountResponse.builder().accountId(7L).documentNumber("xyz").build();
        when(service.findById(7L)).thenReturn(expected);

        AccountResponse actual = controller.get(7L);

        assertSame(expected, actual);
    }
}
