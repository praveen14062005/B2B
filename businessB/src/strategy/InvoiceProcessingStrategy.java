package strategy;

import model.EDITransaction;
import model.TransactionStatus;

/**
 * Concrete Strategy for handling INVOICE transactions.
 */
public class InvoiceProcessingStrategy implements TransactionProcessingStrategy {

    @Override
    public void process(EDITransaction transaction) {
        System.out.println("  [INVOICE] Invoice processed: ID=" + transaction.getId()
                + ", Supplier=" + transaction.getSupplier()
                + ", Amount=" + transaction.getAmount());

        transaction.setStatus(TransactionStatus.PROCESSED);
        transaction.setReason("Accepted");
    }
}
