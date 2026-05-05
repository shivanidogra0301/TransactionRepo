package com.example.transactions.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Defines the supported financial operations within the system.
 * Encapsulates the business rules for determining how different operation types
 * affect an account's balance (e.g., debits vs. credits).
 */
@Getter
@AllArgsConstructor
public enum OperationType {

    NORMAL_PURCHASE(1, "Normal Purchase", true),
    PURCHASE_WITH_INSTALLMENTS(2, "Purchase with installments", true),
    WITHDRAWAL(3, "Withdrawal", true),
    CREDIT_VOUCHER(4, "Credit Voucher", false);

    private final int id;
    private final String description;

    /** Flag indicating if this operation deducts funds from the account. */
    private final boolean negative;

    /**
     * Pre-computed map for O(1) time complexity lookups by ID.
     */
    private static final Map<Integer, OperationType> ID_MAP = Stream.of(values())
            .collect(Collectors.toMap(OperationType::getId, Function.identity()));

    /**
     * Normalizes the mathematical sign of the transaction amount based on the operation type.
     * Purchases and withdrawals are strictly converted to negative values.
     * Credit vouchers are strictly converted to positive values.
     *
     * @param amount the raw {@link BigDecimal} amount provided in the request
     * @return the normalized amount with the correct mathematical sign
     */
    public BigDecimal normalize(BigDecimal amount) {
        if (amount == null) {
            return null;
        }

        // Always take the absolute value first to prevent double-negatives
        // (e.g., if a client accidentally sends -50.00 for a purchase)
        BigDecimal absolute = amount.abs();
        return this.negative ? absolute.negate() : absolute;
    }

    /**
     * Resolves the corresponding {@link OperationType} from its numeric ID.
     *
     * @param id the numeric identifier of the operation type
     * @return the resolved {@link OperationType}
     * @throws IllegalArgumentException if the ID does not map to a recognized operation
     */
    public static OperationType fromId(int id) {
        OperationType type = ID_MAP.get(id);
        if (type == null) {
            throw new IllegalArgumentException("Unknown operation_type_id: " + id);
        }
        return type;
    }
}