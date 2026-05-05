package com.example.transactions;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/** Smoke test that confirms the Spring application context boots cleanly. */
@ActiveProfiles("test")
@SpringBootTest
class MainTest {

    @Test
    void contextLoads() {
        // Intentionally empty: the Spring framework will fail this test
        // automatically if any bean cannot be instantiated.
    }
}
