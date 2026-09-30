package model;

/**
 * Concrete transaction representing a Sales Invoice.
 *
 * Demonstrates Inheritance and Polymorphism.
 */
public class InvoiceTransaction extends EDITransaction {

    public InvoiceTransaction(String id, String supplier, String customer, double amount) {
        super(id, "INVOICE", supplier, customer, amount);
    }
}
