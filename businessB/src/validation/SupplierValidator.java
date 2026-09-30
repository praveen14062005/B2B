package validation;

import exception.ValidationException;
import model.EDITransaction;

/**
 * Chain of Responsibility — Link 2: SupplierValidator.
 *
 * Ensures that the supplier field is not null, empty, or blank.
 */
public class SupplierValidator extends ValidationHandler {

    @Override
    public void handle(EDITransaction transaction) throws ValidationException {
        String supplier = transaction.getSupplier();

        if (supplier == null || supplier.trim().isEmpty()) {
            throw new ValidationException("Supplier cannot be empty");
        }

        // Pass to next validator if exists
        if (next != null) {
            next.handle(transaction);
        }
    }
}
