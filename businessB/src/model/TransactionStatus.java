package model;

/**
 * Represents the lifecycle status of an EDI transaction.
 *
 * RECEIVED  - Transaction has been read from the input file.
 * VALIDATED - Transaction has passed all validation checks.
 * PROCESSED - Transaction has been successfully processed.
 * REJECTED  - Transaction failed validation and was not processed.
 */
public enum TransactionStatus {
    RECEIVED,
    VALIDATED,
    PROCESSED,
    REJECTED
}
