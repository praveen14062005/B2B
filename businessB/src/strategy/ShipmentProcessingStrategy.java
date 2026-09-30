package strategy;

import model.EDITransaction;
import model.TransactionStatus;

/**
 * Concrete Strategy for handling SHIPMENT transactions.
 */
public class ShipmentProcessingStrategy implements TransactionProcessingStrategy {

    @Override
    public void process(EDITransaction transaction) {
        System.out.println("  [SHIPMENT] Shipment processed: ID=" + transaction.getId()
                + ", Supplier=" + transaction.getSupplier()
                + ", Amount=" + transaction.getAmount());

        transaction.setStatus(TransactionStatus.PROCESSED);
        transaction.setReason("Accepted");
    }
}
