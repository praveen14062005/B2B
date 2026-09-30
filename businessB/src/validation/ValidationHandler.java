package validation;

import exception.ValidationException;
import model.EDITransaction;

/**
 * Chain of Responsibility Pattern — Abstract Validation Handler.
 *
 * Each validator in the chain handles one specific rule:
 * - If the rule fails, it throws a ValidationException (stops chain).
 * - If the rule passes, it delegates to the next handler in the chain.
 *
 * Chain: TransactionTypeValidator -> SupplierValidator -> AmountValidator
 */
public abstract class ValidationHandler {

    protected ValidationHandler next;

    /**
     * Sets the next handler in the chain.
     * Returns the next handler to allow method chaining: a.setNext(b).setNext(c)
     */
    public ValidationHandler setNext(ValidationHandler next) {
        this.next = next;
        return next;
    }

    /**
     * Validates the transaction. Subclasses must implement this method.
     *
     * @param transaction the transaction to validate
     * @throws ValidationException if validation rule fails
     */
    public abstract void handle(EDITransaction transaction) throws ValidationException;
}
