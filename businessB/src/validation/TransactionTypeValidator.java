package validation;

import exception.ValidationException;
import model.EDITransaction;

/**
 * Chain of Responsibility — Link 1: TransactionTypeValidator.
 *
 * Checks that the transaction type is one of: ORDER, INVOICE, SHIPMENT.
 */
public class TransactionTypeValidator extends ValidationHandler {

    @Override
    public void handle(EDITransaction transaction) throws ValidationException {
        String type = transaction.getType();

        if (!"ORDER".equals(type) && !"INVOICE".equals(type) && !"SHIPMENT".equals(type)) {
            throw new ValidationException("Unsupported transaction type");
        }

        // Pass to next validator if exists
        if (next != null) {
            next.handle(transaction);
        }
    }
}
