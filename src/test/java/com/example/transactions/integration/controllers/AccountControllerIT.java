package com.example.transactions.integration.controllers;

import com.example.transactions.integration.BaseIntegrationTest;
import com.example.transactions.models.requests.CreateAccountRequest;
import com.example.transactions.models.response.AccountResponse;
import com.example.transactions.models.response.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountControllerIT extends BaseIntegrationTest {

    @Autowired private TestRestTemplate restTemplate;

    @Test
    void createAccount_ShouldReturn201_WhenValid() {
        CreateAccountRequest request = new CreateAccountRequest("112233");
        ResponseEntity<AccountResponse> response = restTemplate.postForEntity("/api/accounts", request, AccountResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody().accountId());
    }

    @Test
    void createAccount_ShouldReturn400_WhenDocumentMissing() {
        CreateAccountRequest request = new CreateAccountRequest(""); // Trigger @NotBlank
        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity("/api/accounts", request, ErrorResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().detail().contains("documentNumber must not be blank"));
    }

    @Test
    void findById_ShouldReturn404_WhenNotFound() {
        ResponseEntity<ErrorResponse> response = restTemplate.getForEntity("/api/accounts/999", ErrorResponse.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().detail().contains("Account not found: 999"));
    }
}