package validation;

import exception.ValidationException;
import model.EDITransaction;

/**
 * Chain of Responsibility — Link 3: AmountValidator.
 *
 * Ensures that the transaction amount is strictly greater than 0.
 */
public class AmountValidator extends ValidationHandler {

    @Override
    public void handle(EDITransaction transaction) throws ValidationException {
        double amount = transaction.getAmount();

        if (amount <= 0) {
            throw new ValidationException("Amount must be greater than 0");
        }

        // Pass to next validator if exists
        if (next != null) {
            next.handle(transaction);
        }
    }
}
