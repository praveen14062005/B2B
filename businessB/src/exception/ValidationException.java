package exception;

/**
 * Custom checked exception thrown when a transaction fails business validation.
 */
public class ValidationException extends Exception {

    public ValidationException(String message) {
        super(message);
    }
}
