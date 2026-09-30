package factory;

import exception.ValidationException;
import model.EDITransaction;
import model.InvoiceTransaction;
import model.OrderTransaction;
import model.ShipmentTransaction;

/**
 * Factory Pattern — EDITransactionFactory.
 *
 * Encapsulates object creation logic.
 * Reads the transaction type and instantiates the correct subclass:
 *   ORDER    -> OrderTransaction
 *   INVOICE  -> InvoiceTransaction
 *   SHIPMENT -> ShipmentTransaction
 *
 * Interview Explanation:
 * "The Factory creates the appropriate transaction object based on the transaction type,
 * so the caller doesn't directly create different subclasses."
 */
public class EDITransactionFactory {

    /**
     * Parses a CSV line and creates the appropriate transaction object.
     *
     * @param line CSV line in format TYPE,ID,SUPPLIER,CUSTOMER,AMOUNT
     * @return Concrete EDITransaction subclass instance
     * @throws ValidationException if format or type is invalid
     * @throws NumberFormatException if amount is not a valid number
     */
    public EDITransaction createTransaction(String line)
            throws ValidationException, NumberFormatException {

        String[] parts = line.split(",");

        // Check required 5 fields: TYPE, ID, SUPPLIER, CUSTOMER, AMOUNT
        if (parts.length != 5) {
            throw new ValidationException("Invalid transaction format: expected 5 fields");
        }

        String type     = parts[0].trim().toUpperCase();
        String id       = parts[1].trim();
        String supplier = parts[2].trim();
        String customer = parts[3].trim();
        double amount   = Double.parseDouble(parts[4].trim());

        switch (type) {
            case "ORDER":
                return new OrderTransaction(id, supplier, customer, amount);
            case "INVOICE":
                return new InvoiceTransaction(id, supplier, customer, amount);
            case "SHIPMENT":
                return new ShipmentTransaction(id, supplier, customer, amount);
            default:
                throw new ValidationException("Unsupported transaction type");
        }
    }
}
