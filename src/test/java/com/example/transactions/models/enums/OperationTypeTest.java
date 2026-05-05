package com.example.transactions.models.enums;

import com.example.transactions.models.enums.OperationType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Pure unit tests covering enum lookup + sign normalisation. */
class OperationTypeTest {

    @ParameterizedTest(name = "{0}({1}) normalises to {2}")
    @CsvSource({
            "NORMAL_PURCHASE,             50.00, -50.00",
            "NORMAL_PURCHASE,            -50.00, -50.00",
            "PURCHASE_WITH_INSTALLMENTS,  20.00, -20.00",
            "WITHDRAWAL,                  75.00, -75.00",
            "CREDIT_VOUCHER,            -100.00, 100.00",
            "CREDIT_VOUCHER,             100.00, 100.00"
    })
    void normalisesAmountAccordingToType(OperationType type, BigDecimal input, BigDecimal expected) {
        assertEquals(expected, type.normalize(input));
    }

    @ParameterizedTest(name = "id {0} -> {1}")
    @CsvSource({
            "1, NORMAL_PURCHASE",
            "2, PURCHASE_WITH_INSTALLMENTS",
            "3, WITHDRAWAL",
            "4, CREDIT_VOUCHER"
    })
    void resolvesEachIdToExpectedConstant(int id, OperationType expected) {
        assertEquals(expected, OperationType.fromId(id));
    }

    @Test
    void rejectsUnknownId() {
        assertThrows(IllegalArgumentException.class, () -> OperationType.fromId(42));
    }

    @Test
    void exposesDescriptionAndId() {
        assertEquals("Withdrawal", OperationType.WITHDRAWAL.getDescription());
        assertEquals(3, OperationType.WITHDRAWAL.getId());
        assertEquals(true, OperationType.WITHDRAWAL.isNegative());
        assertEquals(false, OperationType.CREDIT_VOUCHER.isNegative());
    }
}
