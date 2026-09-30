package model;

/**
 * Concrete transaction representing a Purchase Order.
 *
 * Demonstrates Inheritance and Polymorphism.
 */
public class OrderTransaction extends EDITransaction {

    public OrderTransaction(String id, String supplier, String customer, double amount) {
        super(id, "ORDER", supplier, customer, amount);
    }
}
