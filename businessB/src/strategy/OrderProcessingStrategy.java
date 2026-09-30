package strategy;

import model.EDITransaction;
import model.TransactionStatus;

/**
 * Concrete Strategy for handling ORDER transactions.
 */
public class OrderProcessingStrategy implements TransactionProcessingStrategy {

    @Override
    public void process(EDITransaction transaction) {
        System.out.println("  [ORDER] Order processed: ID=" + transaction.getId()
                + ", Supplier=" + transaction.getSupplier()
                + ", Amount=" + transaction.getAmount());

        transaction.setStatus(TransactionStatus.PROCESSED);
        transaction.setReason("Accepted");
    }
}
