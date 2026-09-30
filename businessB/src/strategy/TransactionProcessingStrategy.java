package strategy;

import model.EDITransaction;

/**
 * Strategy Pattern — TransactionProcessingStrategy Interface.
 *
 * Defines the contract for processing different transaction types.
 * Demonstrates Polymorphism and allows each transaction type
 * to encapsulate its own processing algorithm.
 */
public interface TransactionProcessingStrategy {

    /**
     * Executes processing logic for the given transaction.
     * Updates transaction status to PROCESSED.
     *
     * @param transaction the validated transaction to process
     */
    void process(EDITransaction transaction);
}
