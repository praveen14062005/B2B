package model;

/**
 * Concrete transaction representing an Advanced Shipping Notice (Shipment).
 *
 * Demonstrates Inheritance and Polymorphism.
 */
public class ShipmentTransaction extends EDITransaction {

    public ShipmentTransaction(String id, String supplier, String customer, double amount) {
        super(id, "SHIPMENT", supplier, customer, amount);
    }
}
