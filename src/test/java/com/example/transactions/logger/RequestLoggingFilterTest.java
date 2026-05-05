package com.example.transactions.logger;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RequestLoggingFilterTest {

    private final RequestLoggingFilter filter = new RequestLoggingFilter();

    @Test
    void doFilterInternal_ShouldProceedWithChainAndLog() throws ServletException, IOException {
        // Arrange: Create Mock Request and Response
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setRequestURI("/api/accounts");

        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setStatus(200);

        // We use a spy on the FilterChain to verify the chain is executed
        FilterChain filterChain = mock(FilterChain.class);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert: Verify the chain.doFilter was called exactly once
        verify(filterChain, times(1)).doFilter(request, response);

        // Assert: Verify status remains unchanged
        assertEquals(200, response.getStatus());
    }

    @Test
    void doFilterInternal_ShouldHandleExceptionsAndStillLog() throws ServletException, IOException {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/transactions");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        // Simulate a failure further down the chain (e.g., in a Controller)
        doThrow(new RuntimeException("Database error")).when(filterChain).doFilter(any(), any());

        // Act & Assert
        try {
            filter.doFilterInternal(request, response, filterChain);
        } catch (RuntimeException e) {
            assertEquals("Database error", e.getMessage());
        }

        // Verify that even if an exception occurs, the filter tried to execute the chain
        verify(filterChain, times(1)).doFilter(request, response);
    }
}